package com.cyberlink.cosmetic.error;


public class ErrorResolution extends net.sourceforge.stripes.action.ErrorResolution {
    public ErrorResolution(ErrorDef error) {
		super(error.code(), error.message());
	}
}
