package com.ainq.caliphr.hqmf.model.type;

/**
 * # What does ED stand for?
 * # ED has a lot more elements / attributes to represent, but only caring about what I see used...
 * @author drosenbaum
 *
 */
public class HQMF_ED {

	private String type;
	private String value;
	private String mediaType;
	
	public HQMF_ED(String type, String value, String mediaType) {
		super();
		this.type = type;
		if (type == null) {
			this.type = "ED";
		}
		this.value = value;
		this.mediaType = mediaType;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getValue() {
		return value;
	}

	public void setValue(String value) {
		this.value = value;
	}

	public String getMediaType() {
		return mediaType;
	}

	public void setMediaType(String mediaType) {
		this.mediaType = mediaType;
	}
}
