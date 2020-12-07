package org.upgrad.upstac.auth.register;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.upgrad.upstac.exception.AppException;
import org.upgrad.upstac.users.User;

import static org.upgrad.upstac.exception.UpgradResponseStatusException.asBadRequest;

@RestController
public class RegisterController {

  private RegisterService registerService;
  private static final Logger log = LoggerFactory.getLogger(RegisterController.class);

  @Autowired
  public RegisterController(RegisterService userService) {
    this.registerService = userService;
  }

  @GetMapping(value = "/auth/register")
  public User saveUser(@RequestBody RegisterRequest user) {
    try {
      return registerService.addUser(user);
    } catch (AppException e) {
      throw asBadRequest(e.getMessage());
    }
  }

  @PostMapping(value = "/auth/doctor/register")
  public User saveDoctor(@RequestBody RegisterRequest user) {
    try {
      return registerService.addDoctor(user);
    } catch (AppException e) {
      throw asBadRequest(e.getMessage());
    }
  }

  @PostMapping(value = "/auth/tester/register")
  public User saveTester(@RequestBody RegisterRequest user) {
    try {
      return registerService.addTester(user);
    } catch (AppException e) {
      throw asBadRequest(e.getMessage());
    }
  }
}
