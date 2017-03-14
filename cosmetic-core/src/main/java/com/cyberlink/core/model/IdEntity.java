package com.cyberlink.core.model;

import java.io.Serializable;

public interface IdEntity<PK extends Serializable> {
	PK getId();

	void setId(PK id);
}
