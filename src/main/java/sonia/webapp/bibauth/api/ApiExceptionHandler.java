/*
 * Copyright 2022 Thorsten Ludewig (t.ludewig@gmail.com).
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package sonia.webapp.bibauth.api;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

/**
 *
 * @author Thorsten Ludewig (t.ludewig@gmail.com)
 */
@ControllerAdvice
@RestControllerAdvice
public class ApiExceptionHandler extends ResponseEntityExceptionHandler
{
  public final static String RESPONSE_INVALID_CERDENTIALS = "invalid_credentials";

  public final static String RESPONSE_NOT_FOUND = "not_found";

  public final static String RESPONSE_INVALID_CLIENT = "invalid_client";

  @ExceptionHandler(Exception.class)

  public final ResponseEntity<Object> handleAllExceptions(Exception ex,
    WebRequest request)
  {
    String code = ex.getMessage();

    ErrorResponseObject error
      = new ErrorResponseObject(code, "Password incorrect");

    switch (code)
    {
      case RESPONSE_NOT_FOUND:
        error.setError("User does not exist");
        break;

      case RESPONSE_INVALID_CLIENT:
        error.setError("Client secret is not correct.");
        break;
    }

    return new ResponseEntity(error, HttpStatus.UNAUTHORIZED);
  }
}
