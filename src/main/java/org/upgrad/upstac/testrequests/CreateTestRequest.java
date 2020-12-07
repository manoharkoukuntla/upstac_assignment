package org.upgrad.upstac.testrequests;

import lombok.Data;
import org.upgrad.upstac.users.models.Gender;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

@Data
public class CreateTestRequest {
  @NotBlank
  private String name;

  @NotNull
  private Gender gender;

  @NotBlank
  private String address;

  @NotNull
  private Integer age;

  @NotBlank
  private String email;

  @NotBlank
  private String phoneNumber;

  @NotNull
  private Integer pinCode;
}
