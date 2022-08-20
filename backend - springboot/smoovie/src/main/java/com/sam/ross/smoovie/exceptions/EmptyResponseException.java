package com.sam.ross.smoovie.exceptions;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.ResponseStatus;

@Getter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@ResponseStatus(value= HttpStatus.NO_CONTENT, reason="Empty response returned from the server")
public class EmptyResponseException extends RuntimeException{
    String message;
}
