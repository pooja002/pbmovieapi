package com.pbmovies.exception;

public class EmptyFileException extends RuntimeException{

    public  EmptyFileException(String message)
    {
        super(message);
    }
}
