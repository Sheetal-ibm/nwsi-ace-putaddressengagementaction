package uk.co.nationwide.nem.accval;
class TagSORTYPE {

	private String sortcode;
	private String accType;
	
	public TagSORTYPE (String SortCode, String AccType)
	{
		this.sortcode = SortCode;
		this.accType = AccType;
	}
	
	public String getSortCode() {
		return this.sortcode;
	}
	
	public String getAccType() {
		return this.accType;
	}
}
