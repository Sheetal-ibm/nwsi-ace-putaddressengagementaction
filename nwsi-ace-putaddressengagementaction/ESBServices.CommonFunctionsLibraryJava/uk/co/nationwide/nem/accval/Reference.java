package uk.co.nationwide.nem.accval;
class Reference<T> {

	private T referent;
	
	public Reference(T initialValue) {
		this.referent = initialValue;
	}
	
	public void set (T newVal) {
		this.referent = newVal;
	}
	
	public T get() {
		return this.referent;
	}
	
}
