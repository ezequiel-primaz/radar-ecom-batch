package com.radarecom.radarecom.exception;

public class MLItemNotFound extends RuntimeException{
    public MLItemNotFound(){
        super("Mercado livre item not found");
    }
}
