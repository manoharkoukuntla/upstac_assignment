package org.upgrad.upstac.testrequests;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.validation.annotation.Validated;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.testrequests.consultation.Consultation;
import org.upgrad.upstac.testrequests.consultation.ConsultationService;
import org.upgrad.upstac.testrequests.consultation.CreateConsultationRequest;
import org.upgrad.upstac.testrequests.flow.TestRequestFlowService;
import org.upgrad.upstac.testrequests.lab.LabResult;
import org.upgrad.upstac.testrequests.lab.LabResultService;
import org.upgrad.upstac.testrequests.lab.CreateLabResult;
import org.upgrad.upstac.users.User;

import javax.transaction.Transactional;
import javax.validation.Valid;

import static org.upgrad.upstac.shared.Messages.INVALID_ID;
import static org.upgrad.upstac.shared.Messages.INVALID_ID_OR_STATE;


@Service
@Validated
public class TestRequestUpdateService {

  @Autowired
  private TestRequestRepository testRequestRepository;

  @Autowired
  private TestRequestFlowService testRequestFlowService;

  @Autowired
  private LabResultService labResultService;

  @Autowired
  private ConsultationService consultationService;

  @Transactional
  public TestRequest saveTestRequest(@Valid TestRequest result) {
    return testRequestRepository.save(result);
  }

  TestRequest updateStatusAndSave(TestRequest testRequest, RequestStatus status) {
    testRequest.setStatus(status);
    return saveTestRequest(testRequest);
  }

  public TestRequest assignForLabTest(Long id, User tester) {
    TestRequest testRequest =
        testRequestRepository.findByRequestIdAndStatus(id, RequestStatus.INITIATED)
            .orElseThrow(() -> new AppException(INVALID_ID));
    LabResult labResult = labResultService.assignForLabTest(testRequest, tester);
    testRequestFlowService.log(testRequest, RequestStatus.INITIATED,
        RequestStatus.LAB_TEST_IN_PROGRESS, tester);
    testRequest.setLabResult(labResult);
    return updateStatusAndSave(testRequest, RequestStatus.LAB_TEST_IN_PROGRESS);
  }

  public TestRequest updateLabTest(Long id, @Valid CreateLabResult createLabResult, User tester) {

    TestRequest testRequest =
        testRequestRepository.findByRequestIdAndStatus(id, RequestStatus.LAB_TEST_IN_PROGRESS)
            .orElseThrow(() -> new AppException(INVALID_ID_OR_STATE));
    labResultService.updateLabTest(testRequest, createLabResult);
    testRequestFlowService.log(testRequest, RequestStatus.LAB_TEST_IN_PROGRESS,
        RequestStatus.LAB_TEST_COMPLETED, tester);
    return updateStatusAndSave(testRequest, RequestStatus.LAB_TEST_COMPLETED);
  }

  public TestRequest assignForConsultation(Long id, User doctor) {
    TestRequest testRequest =
        testRequestRepository.findByRequestIdAndStatus(id, RequestStatus.LAB_TEST_COMPLETED)
            .orElseThrow(() -> new AppException(INVALID_ID_OR_STATE));
    Consultation consultation = consultationService.assignForConsultation(testRequest, doctor);
    testRequestFlowService.log(testRequest, RequestStatus.LAB_TEST_COMPLETED,
        RequestStatus.DIAGNOSIS_IN_PROCESS, doctor);
    testRequest.setConsultation(consultation);
    return updateStatusAndSave(testRequest, RequestStatus.DIAGNOSIS_IN_PROCESS);
  }

  public TestRequest updateConsultation(Long id,
      @Valid CreateConsultationRequest createConsultationRequest, User doctor) {

    TestRequest testRequest =
        testRequestRepository.findByRequestIdAndStatus(id, RequestStatus.DIAGNOSIS_IN_PROCESS)
            .orElseThrow(() -> new AppException(INVALID_ID_OR_STATE));
    consultationService.updateConsultation(testRequest, createConsultationRequest);
    testRequestFlowService.log(testRequest, RequestStatus.DIAGNOSIS_IN_PROCESS,
        RequestStatus.COMPLETED, doctor);
    return updateStatusAndSave(testRequest, RequestStatus.COMPLETED);
  }
}
