package com.playerdata.groupFightOnline.uData;

import javax.persistence.Id;
import javax.persistence.Table;

import org.codehaus.jackson.annotate.JsonIgnoreProperties;

import com.playerdata.dataSyn.annotation.SynClass;


@SynClass
@JsonIgnoreProperties(ignoreUnknown = true)
@Table(name = "gf_online_group")
public class GFightOnlineGroupData {
	
	@Id
	private int groupID;
	
	
	
	
}
