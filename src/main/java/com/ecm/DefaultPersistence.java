package com.ecm;


import javax.persistence.Embedded;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.io.Serializable;

@MappedSuperclass
public abstract class DefaultPersistence implements Serializable {
	private String id;
	private LogInformation logInformation = new LogInformation();
	
	//private String siteId;
	
	/**
	 * @return Returns the id.
	 * @hibernate.id generator-class="uuid.hex" length="28" unsaved-value="0"
	 */
	//@Id() @GeneratedValue(generator="system-uuid")
	//@GenericGenerator(name="system-uuid", strategy = "uuid")
	public String getId() {
		return id;
	}
	/**
	 * @param id The id to set.
	 */
	public void setId(String id) {
        if(id == null || id.equals("")){
            id = "dfajfajfj1121";
        }
		this.id = id;
	}
	/**
	 * @return Returns the logInformation.
	 * @hibernate.component
	 */
	@Embedded
	public LogInformation getLogInformation() {
		return logInformation;
	}
	/**
	 * @param logInformation The logInformation to set.
	 */
	public void setLogInformation(LogInformation logInformation) {
		this.logInformation = logInformation;
	}
}
