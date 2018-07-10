package WebExecuter;

import javafx.beans.property.SimpleStringProperty;

public class WE_Item {
	private SimpleStringProperty host;
	private SimpleStringProperty url;
	private SimpleStringProperty browser;

	public WE_Item( String host, String url, String browser ) {
		this.host = new SimpleStringProperty( host );
		this.url = new SimpleStringProperty( url );
		this.browser = new SimpleStringProperty( browser );
	}

	public void setHost( String host ) {
		this.host.set( host );
	}

	public String getHost() {
		return this.host.get();
	}

	public void setUrl( String url ) {
		this.url.set( url );
	}

	public String getUrl() {
		return this.url.get();
	}

	public void setBrowser( String browser ) {
		this.browser.set( browser );
	}

	public String getBrowser() {
		return this.browser.get();
	}

	public String getSaveData() {
		StringBuilder sb = new StringBuilder();
		sb.append( this.host.get() );
		sb.append(",");
		sb.append( this.url.get() );
		sb.append(",");
		sb.append( this.browser.get() );
		return sb.toString();
	}

}
