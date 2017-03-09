package com.cognizant.gto.ccap.plugin.refactormarker;

public class MarkerPayload {
	public MarkerPayload() {
		super();
	}

	private String elementName = MarkerFactory.STRING_NA;
	private short endColumn = MarkerFactory.INT_ZERO;
	private int endLine = MarkerFactory.INT_ZERO;
	private String filePath = MarkerFactory.STRING_NA;
	private short priority = MarkerFactory.INT_ZERO;
	private String projectName = MarkerFactory.STRING_NA;
	private String qualifiedTypeName = MarkerFactory.STRING_NA;
	private boolean resolutionFound = Boolean.FALSE;
	private String ruleInfo = MarkerFactory.STRING_NA;
	private String ruleName = MarkerFactory.STRING_NA;
	private short startColumn = MarkerFactory.INT_ZERO;
	private int startLine = MarkerFactory.INT_ZERO;

	public MarkerPayload(String elementName, short endColumn, int endLine,
			String filePath, short priority, String projectName,
			String qualifiedTypeName, boolean resolutionFound, String ruleInfo,
			String ruleName, short startColumn, int startLine) {
		this.elementName = elementName;
		this.endColumn = endColumn;
		this.endLine = endLine;
		this.filePath = filePath;
		this.priority = priority;
		this.projectName = projectName;
		this.qualifiedTypeName = qualifiedTypeName;
		this.resolutionFound = resolutionFound;
		this.ruleInfo = ruleInfo;
		this.ruleName = ruleName;
		this.startColumn = startColumn;
		this.startLine = startLine;
	}

	private String generateMarkerPayloadString() {
		StringBuilder builder = new StringBuilder("[MarkerPayload:{");

		if (null != elementName && !elementName.isEmpty()) {
			builder.append("(elementName=" + elementName + "),");
		}

		builder.append("(endColumn=" + endColumn + "),");
		builder.append("(endLine=" + endLine + "),");

		if (null != filePath && !filePath.isEmpty()) {
			builder.append("(filePath=" + filePath + "),");
		}

		builder.append("(priority=" + priority + "),");

		if (null != projectName && !projectName.isEmpty()) {
			builder.append("(projectName=" + projectName + "),");
		}

		if (null != qualifiedTypeName && !qualifiedTypeName.isEmpty()) {
			builder.append("(qualifiedTypeName=" + qualifiedTypeName + "),");
		}

		builder.append("(resolutionFound=" + resolutionFound + "),");

		if (null != ruleInfo && !ruleInfo.isEmpty()) {
			builder.append("(ruleInfo=" + ruleInfo + "),");
		}

		if (null != ruleName && !ruleName.isEmpty()) {
			builder.append("(ruleName=" + ruleName + "),");
		}

		builder.append("(startColumn=" + startColumn + "),");
		builder.append("(startLine=" + startLine + ")");

		return builder.append("}]").toString();
	}

	public String getElementName() {
		return elementName;
	}

	public short getEndColumn() {
		return endColumn;
	}

	public int getEndLine() {
		return endLine;
	}

	public String getFilePath() {
		return filePath;
	}

	public short getPriority() {
		return priority;
	}

	public String getProjectName() {
		return projectName;
	}

	public String getQualifiedTypeName() {
		return qualifiedTypeName;
	}

	public String getRuleInfo() {
		return ruleInfo;
	}

	public String getRuleName() {
		return ruleName;
	}

	public short getStartColumn() {
		return startColumn;
	}

	public int getStartLine() {
		return startLine;
	}

	public boolean isResolutionFound() {
		return resolutionFound;
	}

	@Override
	public String toString() {
		return generateMarkerPayloadString();
	}

}
