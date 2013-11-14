package com.pinternals.mdmr;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintStream;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.Formatter;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.logging.Logger;

import com.sap.mdm.commands.CommandException;
import com.sap.mdm.extension.session.ConnectionManagerEx;
import com.sap.mdm.ids.ReverseLookupId;
import com.sap.mdm.ids.TupleDefinitionId;
import com.sap.mdm.net.ConnectionException;
import com.sap.mdm.net.MdmSSLException;
import com.sap.mdm.repository.XMLSchemaProperties;
import com.sap.mdm.repository.commands.GetXMLSchemaListCommand;
import com.sap.mdm.schema.FieldProperties;
import com.sap.mdm.schema.RelationshipProperties;
import com.sap.mdm.schema.RepositorySchema;
import com.sap.mdm.schema.TableProperties;
import com.sap.mdm.schema.TableSchema;
import com.sap.mdm.schema.TupleDefinitionProperties;
import com.sap.mdm.schema.TupleDefinitionSchema;
import com.sap.mdm.schema.commands.GetRepositorySchemaCommand;
import com.sap.mdm.schema.fields.LookupFieldProperties;
import com.sap.mdm.server.RepositoryIdentifier;
import com.sap.mdm.session.NonNetWeaverMdmDestinationProperties;
import com.sap.mdm.session.SessionException;
import com.sap.mdm.session.UserSessionContext;


class MdmClient {
	private static Logger log = Logger.getLogger(MdmClient.class.getName());
	public static String locRU = "Russian [RU]";
	public static String locEN_US = "English [US]";
	static String appName = "Mdmr";

	UserSessionContext ctx = null;
//	RepositoryIdentifier[] reps = null;
	NonNetWeaverMdmDestinationProperties destination = null;
	public static String getClientAPIversion() {
		return com.sap.mdm.util.BuildVersion.getBuildVersion();
	}
		
	public UserSessionContext connectStandaloneApp(
			String serverName,
			String repositoryName, String regionName,
			String repositoryUser, String repositoryPassword,
			int unicodeNormType, boolean useBlobCache)  
		throws SessionException, MdmSSLException, ConnectionException {
		/*
		 * Create an instance of NonNetWeaverMdmDestinationProperties
		 *(implementation of MdmDestinationProperties for non-NetWeaver
		 * destinations)
		 */
		destination = new NonNetWeaverMdmDestinationProperties(serverName, repositoryName, repositoryUser);
			 
		// Connect to MDS
		ctx = ConnectionManagerEx.connectWithUserSession(
			destination, repositoryPassword, appName, regionName,
	        null, unicodeNormType, useBlobCache, null);
		
//        SimpleConnection conAccessor = SimpleConnectionFactory.getInstance(mdsName);
//		servSessCmd = new CreateServerSessionCommand(conAccessor);
//		servSessCmd.execute();
//
//		GetRunningRepositoryListCommand cmdRepList = new GetRunningRepositoryListCommand(con);
//		cmdRepList.execute();
//		reps = cmdRepList.getRepositories();

		return ctx;
	}
	void doReport(PrintStream ps, RepositorySchema rs, XMLSchemaProperties[] xsp) {
		ps.println(rs.getRepository().getName());

		// Tables
//		String[] tableCodes = ;
		for (String tc: rs.getTableCodes()) {
			ps.println("Table:\t"+tc);
			TableProperties tp = rs.getTable(tc);
//			ps.println("TableProperties:\t" + tp);
			ps.println("type:\t" + tp.getType() + "\t" + tp.getTableTypeName());
			
			ps.println("code:\t" + tp.getCode());
			ps.println("desc:\t" + tp.getDescription());
			ps.println("name:\t" + tp.getName());
			ps.println("isEditable:\t" + tp.isEditable()); 
			ps.println("isKeyMappable:\t" + tp.isKeyMappable());
			
			FieldProperties[] fs = rs.getFields(tc);
			for (FieldProperties f: fs) {
				ps.println("field:\t" + f);
				ps.println("\tgetCode:\t" + f.getCode());
				ps.println("\tdesc:\t" + f.getDescription());
				ps.println("\tpos:\t" + f.getPosition());
//				ps.println("\t" + f.getSortIndex());
				ps.println("\ttype:\t" + f.getType() + " " + f.getTypeName());
				ps.println("\tCalcId:\t" + f.getCalculationId());
				ps.println("\tContId:\t" + f.getContainerId());
				ps.println("\tname:\t" + f.getName());
				
				ps.println("\tisCalculated:\t" + f.isCalculated());
				ps.println("\tisEditable:\t" + f.isEditable());
				ps.println("\tfreeformsearch:\t" + f.isFreeFormSearchable());
				ps.println("\tisHierarchyLookup:" + f.isHierarchyLookup());
				ps.println("\tkeywordindexable:" + f.isKeywordIndexable());
				ps.println("\tlargeobjlookup:\t" + f.isLargeObjectLookup());
				ps.println("\tisLookup:\t" + f.isLookup());
				ps.println("\tmodOnce:\t" + f.isModifyOnce());
				ps.println("\tmultiling:\t" + f.isMultiLingual());
				ps.println("\tmultival:\t" + f.isMultiValued());
				ps.println("\tpicklist:\t" + f.isPicklistSearchable());
				ps.println("\tisQualified:" + f.isQualified());
				ps.println("\tisQualifier:" + f.isQualifier());
				ps.println("\tisQualifierCached:" + f.isQualifierCached());
				ps.println("\trequired:\t" + f.isRequired());
//				ps.println("\t" + f.isSearchable());
				ps.println("\tshowsearchtab:\t" + f.isShowInSearchTab());
				ps.println("\tsortable:\t" + f.isSortable());
				ps.println("\tisTaxonomyLookup:\t" + f.isTaxonomyLookup());
				ps.println("\tisTuple:\t" + f.isTuple());
				
				if (f.isLookup() && !f.isHierarchyLookup()) {
					LookupFieldProperties lif = (LookupFieldProperties)f;
					ps.println("\tLOOKUP TABLE ID:\t" + lif.getLookupTableId() + rs.getTable(lif.getLookupTableId()));
				} else if (f.isHierarchyLookup()) {
					
				}
			}
			
			TableSchema ts = rs.getTableSchema(tc);
			ps.println(ts);
//			for (String x: ts.getFieldCodes()) ps.println(x);
			for (ReverseLookupId rid: ts.getReverseLookups()) {
				ps.println("ReverseLookup:\t"+rid);
				ps.println(rid.getTableId() + " == " + rs.getTable(rid.getTableId()).getName());
				ps.println(rid.getFieldPath());
			}
			
			ps.println("Qualified:\t"+ts.getQualifiedFields());
			for (FieldProperties x: ts.getQualifiedFields()) ps.print(x + "\t");
			
			ps.println("\nQualifier:\t"+ts.getQualifierFields());
			for (FieldProperties x: ts.getQualifierFields()) ps.print(x + "\t");
			
			ps.println("\nTaxonomy:\t"+ts.getTaxonomyFields());
			for (FieldProperties x: ts.getTaxonomyFields()) ps.print(x + "\t");

			ps.println("\n");
		}

		ps.println("Relationships:\t" + rs.getRelationships() + "\t" + rs.getRelationships().length);
		for (RelationshipProperties rlx: rs.getRelationships()) {
			ps.println("Relationship:\t" + rlx);
		}
		ps.println("Relationships:\t" + rs.getRelationshipCodes() + "\t" + rs.getRelationshipCodes().length);
		for (String x: rs.getRelationshipCodes()) {
			ps.println(x);
		}
	}
	
	void doReportDotV1(PrintStream ps, RepositorySchema rs, XMLSchemaProperties[] xsp) {
		StringBuilder sb = new StringBuilder(65536);
		sb.append("digraph G{\n");
		sb.append("node [shape=oval];\n");
		
		for (String tc: rs.getTableCodes()) {
			TableProperties tp = rs.getTable(tc);
			String code = tp.getCode();
			sb.append(code + ";\n");
			Map<String,String> lookupTo = new HashMap<String,String>();
			for (FieldProperties fp: rs.getFields(tc)) {
				if (fp.isLookup() && !fp.isHierarchyLookup()) {
					LookupFieldProperties lfp = (LookupFieldProperties)fp;
					String codeTo = rs.getTableCode(lfp.getLookupTableId());
					String prev = lookupTo.get(codeTo);
					if (prev==null)
						lookupTo.put(codeTo, fp.getName().toString());
					else 
						lookupTo.put(codeTo, prev + "," + fp.getName().toString());
				}
			}
			for (TupleDefinitionId t: rs.getTupleDefinitionIds()) {
				TupleDefinitionProperties d = rs.getTupleDefinition(t);
			}
			for (String to: lookupTo.keySet()) {
				sb.append(code + "->" + to + ";\n");
			}
			
			
		}
		sb.append("}");
		ps.println(sb);
	}

	
	void getRepositoryInformation(String repname, File f) {
		RepositorySchema rs = null;
		XMLSchemaProperties[] xsp = null;
//		GetRepositoryPropertiesCommand gRPC = null;
//		RepositoryProperties rp = null;
		RepositoryIdentifier rif = ctx.getRepository();
		
		GetRepositorySchemaCommand gRSC = null;
		GetXMLSchemaListCommand gXSC = null;
		try {
			gRSC = new GetRepositorySchemaCommand(ctx);
			gRSC.execute();
			rs = gRSC.getRepositorySchema();
			
			gXSC = new GetXMLSchemaListCommand(ctx); 
			gXSC.execute();
			xsp = gXSC.getXMLSchemas();
			doReportDotV1(new PrintStream(f), rs, xsp);
		} catch (ConnectionException ce) {
			ce.printStackTrace();
		} catch (CommandException ce) {
			ce.printStackTrace();
		} catch (FileNotFoundException ce) {
			ce.printStackTrace();
		}
	}
}

public class Report {
	public static String version = "0.01";
	private static ResourceBundle RES_MSG = ResourceBundle.getBundle("com.pinternals.mdmr.messages");
	private static Logger log = Logger.getLogger(Report.class.getName());

	/**
	 * @param key имя строки в messages.properties
	 * @param args параметры для форматирования
	 * @return строка для пользователя или для логов
	 */
	public static String format(String key, Object... args) {
		return new Formatter().format(RES_MSG.getString(key), args).toString();
	}

	static void logInfo(String res, Object ... args) {
		log.info(format(res, args));
	}
	static void logWarn(String res, Object ... args) {
		log.warning(format(res, args));
	}
	static void logSevere(String res, Object ... args) {
		log.severe(format(res, args));
	}
	
	void getInfo(String host, String repName, String region, String repUser, String repPasswd)
	throws UnknownHostException, IOException, SessionException, MdmSSLException, ConnectionException {
		MdmClient mdmCl = new MdmClient();
		boolean hR = InetAddress.getByName(host).isReachable(10 * 1000); // 10s
		mdmCl.connectStandaloneApp(host, repName, region, repUser, repPasswd, com.sap.mdm.commands.SetUnicodeNormalizationCommand.NORMALIZATION_COMPOSED, false);
		File f = new File(repName);
		f.createNewFile();
		
		mdmCl.getRepositoryInformation(repName, f);
	}
}
