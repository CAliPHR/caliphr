package com.ainq.caliphr.hqmf.model.type;

public class HQMFIdentifier {
	
	private String type;
	private String root;
	private String extension;
	
	public HQMFIdentifier() {
		
	}
	
	public HQMFIdentifier(String type, String root) {
		this(type, root, null);
	}

	public HQMFIdentifier(String type, String root, String extension) {
		super();
		this.type = type;
		this.root = root;
		this.extension = extension;
	}

	public String getType() {
		return type;
	}

	public void setType(String type) {
		this.type = type;
	}

	public String getRoot() {
		return root;
	}

	public void setRoot(String root) {
		this.root = root;
	}

	public String getExtension() {
		return extension;
	}

	public void setExtension(String extension) {
		this.extension = extension;
	}

}
