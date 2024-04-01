package com.pbmovies.exception;

public class MovieNotFoundException extends RuntimeException{

    public MovieNotFoundException(String message)
    {
        super(message);
    }
}

