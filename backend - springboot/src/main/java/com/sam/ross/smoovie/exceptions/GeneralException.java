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
@ResponseStatus(value= HttpStatus.INTERNAL_SERVER_ERROR, reason="General exception thrown")
public class GeneralException extends RuntimeException{
    String message;
}
