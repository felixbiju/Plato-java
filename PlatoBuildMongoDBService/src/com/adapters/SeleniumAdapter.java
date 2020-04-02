package com.adapters;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.LinkedHashMap;

import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.openxml4j.exceptions.InvalidFormatException;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.mongo.constants.GlobalConstants;

public class SeleniumAdapter {
	// for utica
	/*
	 * public JSONArray getSeleniumReport(String reportPath,String jobName,int
	 * nextBuildNumber) throws
	 * IOException,SecurityException,NullPointerException,NumberFormatException,
	 * StringIndexOutOfBoundsException,ArrayIndexOutOfBoundsException,
	 * InvalidFormatException { System.out.println("inside getSeleniumReport");
	 * JSONObject tool=new JSONObject(); JSONArray toolReport=new JSONArray();
	 * File f=new File(reportPath+"/Report/"); File[] fileList=f.listFiles();
	 * 
	 * for(File file:fileList) { if(file.isFile()) { f=file;
	 * 
	 * 
	 * StringBuilder contentBuilder = new StringBuilder(); try { BufferedReader
	 * in = new BufferedReader(new FileReader(f.toString())); String str; while
	 * ((str = in.readLine()) != null) { contentBuilder.append(str); }
	 * in.close(); } catch (IOException e) { } String content =
	 * contentBuilder.toString();
	 * 
	 * try{ Document html = Jsoup.parse(content); String title =
	 * html.title().replaceAll("LTI - ", "").replaceAll(" Automation",
	 * "").trim(); String h1 = html.body().getElementsByTag("h1").text(); //
	 * System.out.println("Input HTML String to JSoup :" + HTMLSTring); //
	 * System.out.println("After parsing, Title : " + title); //
	 * System.out.println("Afte parsing, Heading : " + h1);
	 * 
	 * Element div = html.getElementById("charts-row"); Element
	 * e=div.child(0).child(0); System.out.println(e.child(2).child(0).text());
	 * System.out.println(e.child(3).child(0).text());
	 * System.out.println(e.child(3).html());
	 * 
	 * // String str=e.text().replaceAll("test", "").replaceAll("passed",
	 * "").replaceAll("failed", "").replaceAll("Tests", ""); //
	 * str=str.replaceAll("\\(s\\)", ""); //
	 * System.out.println("*************"+str);
	 * 
	 * tool.put("tool_name", title);
	 * 
	 * 
	 * JSONObject toolRe=new JSONObject(); toolRe.put("chart_name", title);
	 * 
	 * int totexcetime=0;
	 * 
	 * JSONArray chart_labels=new JSONArray(); chart_labels.add("passed");
	 * chart_labels.add("failed");
	 * 
	 * JSONArray chart_values=new JSONArray(); String
	 * value=e.child(2).child(0).text().trim().split(" ")[0].trim(); int val=0;
	 * 
	 * try { val=Integer.parseInt(value); } catch (Exception e1) { // TODO
	 * Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * int val2=0; String value2=e.child(3).child(0).text().trim(); try {
	 * val2=Integer.parseInt(value2); } catch (Exception e1) { // TODO
	 * Auto-generated catch block e1.printStackTrace(); }
	 * 
	 * chart_values.add(""+val); chart_values.add(""+val2);
	 * 
	 * toolRe.put("chart_labels", chart_labels); toolRe.put("chart_values",
	 * chart_values);
	 * 
	 * Element testColl = html.getElementById("test-collection");
	 * 
	 * Elements testAll=testColl.children();
	 * 
	 * JSONArray tabularAllJSON=new JSONArray();
	 * toolRe.put("total_scenario_executed", testAll.size());
	 * 
	 * for(int i=0;i<testAll.size();i++) { JSONObject tablejson=new
	 * JSONObject();
	 * 
	 * 
	 * int fail=0,pass=0,other=0; Element test=testAll.get(i); Element
	 * testHeader=test.child(0); Element name=testHeader.child(0); Element
	 * time=testHeader.child(1); Element status=testHeader.child(2);
	 * 
	 * System.out.println(name.text()+ " : "+ time.text()+" : "+status.text());
	 * tablejson.put("pageTitle", name.text());
	 * tablejson.put("OverAll_Status",status.text());
	 * 
	 * 
	 * Element testbody=test.child(1); Element
	 * testinfo=testbody.child(0).child(2); String
	 * exectime=testinfo.text().split(" ")[1].split("m")[0].trim();
	 * totexcetime+=Integer.parseInt(exectime);
	 * 
	 * Element teststep=testbody.child(1); Element testtable=teststep.child(0);
	 * Element testtablebody=testtable.child(1);
	 * 
	 * Elements testTableAll=testtablebody.children();
	 * 
	 * JSONArray componentAllJSON=new JSONArray(); JSONObject comp=null;
	 * JSONArray componentIndJSON=null; for(int j=0;j<testTableAll.size();j++) {
	 * 
	 * Element trdummy=testTableAll.get(j); String
	 * tablestatusdummy=trdummy.child(0).text(); String
	 * tablestepdummy=trdummy.child(2).text(); int flagstatus=0;
	 * if(tablestatusdummy.trim().equals("info_outline")) { comp=new
	 * JSONObject(); comp.put("componentid", tablestepdummy);
	 * componentIndJSON=new JSONArray(); } for(int
	 * k=j+1;k<testTableAll.size();k++) {
	 * 
	 * Element tr=testTableAll.get(k); String tablestatus=tr.child(0).text();
	 * String tabletime=tr.child(1).text(); String tablestep=tr.child(2).text();
	 * Element tablestepImgElement=tr.child(2).getElementsByTag("img").first();
	 * System.out.println("tablestep is "+tablestep); JSONObject screenshot=new
	 * JSONObject(); if(tablestepImgElement!=null) { String
	 * tablestepImg=tablestepImgElement.attr("data-featherlight");
	 * System.out.println("tablestepImg is "+tablestepImg); String[]
	 * names=tablestepImg.split("\\\\"); String
	 * screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.
	 * JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/Report/";
	 * screenShotUrl+="Screenshots/"+title+"/"+name.text().trim()+"/"+names[
	 * names.length-1];
	 * System.out.println("names[names.length-1]="+names[names.length-1]);
	 * screenshot.put("screenshot", screenShotUrl);
	 * 
	 * } if(tablestatus.trim().equals("info_outline")) { j=(k-1); break; } else
	 * if(k+1==testTableAll.size()) { JSONArray data=new JSONArray(); JSONObject
	 * timeStamp=new JSONObject(); JSONObject details=new JSONObject();
	 * JSONObject statusData=new JSONObject();
	 * System.out.println("tablestepImgElement is "+tablestepImgElement);
	 * System.out.println("table status is "+tablestatus);
	 * timeStamp.put("Timestamp", tabletime);
	 * statusData.put("status",tablestatus); details.put("Details", tablestep);
	 * 
	 * if(tablestatus.trim().equals("cancel")) {
	 * System.out.println("status is fail"); statusData.put("status","fail");
	 * }else if(tablestatus.trim().equals("check_circle")){
	 * System.out.println("status is pass"); statusData.put("status","pass");
	 * }else { statusData.put("status","others"); }
	 * 
	 * // if(tablestep.startsWith("Navigated to")) // {
	 * System.out.println("starts with navigated to"); // String
	 * screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.
	 * JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/"; //
	 * screenShotUrl+="Screenshots/"+title+"/"+name.text().trim()+"/"+tablestep.
	 * replaceAll("Navigated to", "").trim()+".png"; //
	 * screenshot.put("screenshot", screenShotUrl); // // } // else //
	 * if(tablestatus.trim().equals("cancel")) // { // String
	 * screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.
	 * JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/"; //
	 * screenShotUrl+="Screenshots/"+title+"/"+name.text().trim()+"/Error.png";
	 * // screenshot.put("screenshot", screenShotUrl); // flagstatus=1; // } //
	 * else // { // screenshot.put("screenshot", ""); // }
	 * 
	 * data.add(screenshot); data.add(timeStamp); data.add(details);
	 * data.add(statusData); componentIndJSON.add(data); j=testTableAll.size();
	 * break; }
	 * 
	 * if(tablestatus.trim().equals("cancel")) { flagstatus=1; fail++; } else
	 * if(tablestatus.trim().equals("check_circle")) { pass++; } else other++;
	 * 
	 * JSONArray data=new JSONArray(); JSONObject timeStamp=new JSONObject();
	 * JSONObject details=new JSONObject(); JSONObject statusData=new
	 * JSONObject(); // JSONObject screenshot=new JSONObject();
	 * 
	 * // if(tablestep.startsWith("Navigated to ")) // { // String
	 * screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.
	 * JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/"; //
	 * screenShotUrl+="Screenshots/"+title+"/"+name.text().trim()+"/"+tablestep.
	 * replaceAll("Navigated to", "").trim()+".png"; //
	 * screenshot.put("screenshot", screenShotUrl); // // } // else //
	 * if(tablestatus.trim().equals("cancel")) // { // String
	 * screenShotUrl=GlobalConstants.JENKINS_URL+":"+GlobalConstants.
	 * JENKINS_PORT+"/jenkins/job/"+jobName+"/ws/"+nextBuildNumber+"/"; //
	 * screenShotUrl+="Screenshots/"+title+"/"+name.text().trim()+"/Error.png";
	 * // screenshot.put("screenshot", screenShotUrl); // } // else // { //
	 * screenshot.put("screenshot", ""); // }
	 * 
	 * System.out.println("table status is "+tablestatus);
	 * timeStamp.put("Timestamp", tabletime); details.put("Details", tablestep);
	 * if(tablestatus.trim().equals("cancel")) {
	 * statusData.put("status","fail"); }else
	 * if(tablestatus.trim().equals("check_circle")){
	 * statusData.put("status","pass"); }else {
	 * statusData.put("status","others"); }
	 * 
	 * data.add(screenshot); data.add(timeStamp); data.add(details);
	 * data.add(statusData); componentIndJSON.add(data); // JSONObject data=new
	 * JSONObject();
	 * 
	 * // data.put("Timestamp", tabletime); // data.put("Details", tablestep);
	 * // componentIndJSON.add(data); } comp.put("componentsArray",
	 * componentIndJSON); if(flagstatus==1) comp.put("overallStatus", "fail");
	 * else comp.put("overallStatus", "pass");
	 * 
	 * 
	 * componentAllJSON.add(comp); } tablejson.put("total_test_cases",
	 * fail+pass+other); tablejson.put("failed", fail); tablejson.put("passed",
	 * pass);
	 * 
	 * tablejson.put("components", componentAllJSON);
	 * tabularAllJSON.add(tablejson); }
	 * toolRe.put("total_execution_time",totexcetime+"Min");
	 * 
	 * toolRe.put("tabular_data", tabularAllJSON); toolReport.add(toolRe);
	 * //tool.put("ToolReport", toolReport); } catch(Exception e) {
	 * e.printStackTrace(); }
	 * 
	 * } tool.put("ToolReport", toolReport); }
	 * 
	 * return toolReport;
	 * 
	 * }
	 */

	// for RSA

	@SuppressWarnings("unchecked")
	public JSONArray getSeleniumReport(String reportPath, String jobName, int nextBuildNumber)
			throws IOException, SecurityException, NullPointerException, NumberFormatException,
			StringIndexOutOfBoundsException, ArrayIndexOutOfBoundsException, InvalidFormatException,Exception {
		System.out.println("inside getSeleniumReport");
		JSONObject tool = new JSONObject();
		JSONArray toolReport = new JSONArray();
//		reportPath = "D:/AllReports/Bajaj/Selenium/Report";
		File f = new File(reportPath);
		File[] fileList = f.listFiles();

		for (File file : fileList) {
			if (file.isFile()) {
				f = file;

				StringBuilder contentBuilder = new StringBuilder();
				try {
					BufferedReader in = new BufferedReader(new FileReader(f.toString()));
					String str;
					while ((str = in.readLine()) != null) {
						contentBuilder.append(str);
					}
					in.close();
				} catch (IOException e) {
				}
				String content = contentBuilder.toString();

				try {
					Document html = Jsoup.parse(content);
					String title = html.title().replaceAll("LTI - ", "").replaceAll(" Automation", "").trim();

					Element div = html.getElementById("charts-row");
					Element e = div.child(0).child(0);
					System.out.println(e.child(2).child(0).text());
					System.out.println(e.child(3).child(0).text());
					System.out.println(e.child(3).html());

					tool.put("tool_name", title);

					JSONObject toolRe = new JSONObject();
					toolRe.put("chart_name", title);

					int totexcetime = 0;

					JSONArray chart_labels = new JSONArray();
					chart_labels.add("passed");
					chart_labels.add("failed");

					JSONArray chart_values = new JSONArray();
					String value = e.child(2).child(0).text().trim().split(" ")[0].trim();
					int val = 0;

					try {
						val = Integer.parseInt(value);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					int val2 = 0;
					String value2 = e.child(3).child(0).text().trim();
					try {
						val2 = Integer.parseInt(value2);
					} catch (Exception e1) {
						e1.printStackTrace();
					}

					chart_values.add("" + val);
					chart_values.add("" + val2);

					toolRe.put("chart_labels", chart_labels);
					toolRe.put("chart_values", chart_values);

					Element testColl = html.getElementById("test-collection");

					Elements testAll = testColl.children();

					JSONArray tabularAllJSON = new JSONArray();
					toolRe.put("total_scenario_executed", testAll.size());

					for (int i = 0; i < testAll.size(); i++) {
						JSONObject tablejson = new JSONObject();

						int fail = 0, pass = 0, other = 0;
						Element test = testAll.get(i);
						Element testHeader = test.child(0);
						Element name = testHeader.child(0);
						Element time = testHeader.child(1);
						Element status = testHeader.child(2);

						System.out.println(name.text() + " : " + time.text() + " : " + status.text());
						tablejson.put("pageTitle", name.text());
						tablejson.put("OverAll_Status", status.text());

						Element testbody = test.child(1);
						Element testinfo = testbody.child(0).child(2);
						String exectime = testinfo.text().split(" ")[1].split("m")[0].trim();
						totexcetime += Integer.parseInt(exectime);

						Element teststep = testbody.child(1);
						Element testtable = teststep.child(0);
						Element testtablebody = testtable.child(1);

						Elements testTableAll = testtablebody.children();

						JSONArray componentAllJSON = new JSONArray();
						JSONObject comp = null;
						JSONArray componentIndJSON = null;
						for (int j = 0; j < testTableAll.size(); j++) {

							Element trdummy = testTableAll.get(j);
							String tablestatusdummy = trdummy.child(0).text();
							String tablestepdummy = trdummy.child(2).text();
							int flagstatus = 0;
							if (tablestatusdummy.trim().equals("info_outline")) {
								comp = new JSONObject();
								comp.put("componentid", tablestepdummy);
								componentIndJSON = new JSONArray();
							}
							for (int k = j + 1; k < testTableAll.size(); k++) {

								Element tr = testTableAll.get(k);
								String tablestatus = tr.child(0).text();
								String tabletime = tr.child(1).text();
								String tablestep = tr.child(2).text();
								System.out.println("1.tablestep is " + tablestep);
								if (tablestatus.trim().equals("info_outline")) {
									j = (k - 1);
									break;
								} else if (k + 1 == testTableAll.size()) {
									JSONArray data = new JSONArray();
									JSONObject timeStamp = new JSONObject();
									JSONObject details = new JSONObject();
									JSONObject statusData = new JSONObject();
									JSONObject screenshot = new JSONObject();
									System.out.println("table status is " + tablestatus);
									timeStamp.put("Timestamp", tabletime);
									statusData.put("status", tablestatus);
									details.put("Details", tablestep);

									if (tablestatus.trim().equals("cancel")) {
										System.out.println("status is fail");
										statusData.put("status", "fail");
									} else if (tablestatus.trim().equals("check_circle")) {
										System.out.println("status is pass");
										statusData.put("status", "pass");
									} else {
										statusData.put("status", "others");
									}

									if (tablestep.startsWith("Navigated to")) {
										System.out.println("starts with navigated to");
										String screenShotUrl = GlobalConstants.JENKINS_URL + ":"
												+ GlobalConstants.JENKINS_PORT + "/jenkins/job/" + jobName + "/ws/"
												+ nextBuildNumber + "/";
										screenShotUrl += "Screenshots/" + title + "/" + name.text().trim() + "/"
												+ tablestep.replaceAll("Navigated to", "").trim() + ".png";
										screenshot.put("screenshot", screenShotUrl);

									} else if (tablestatus.trim().equals("cancel")) {
										String screenShotUrl = GlobalConstants.JENKINS_URL + ":"
												+ GlobalConstants.JENKINS_PORT + "/jenkins/job/" + jobName + "/ws/"
												+ nextBuildNumber + "/";
										screenShotUrl += "Screenshots/" + title + "/" + name.text().trim()
												+ "/Error.png";
										screenshot.put("screenshot", screenShotUrl);
										flagstatus = 1;
									} else {
										screenshot.put("screenshot", "");
									}
									System.out
											.println("2.tablestep is " + tablestep + " tablestatus is " + tablestatus);

									data.add(screenshot);
									data.add(timeStamp);
									data.add(details);
									data.add(statusData);
									componentIndJSON.add(data);
									j = testTableAll.size();
									break;
								}

								if (tablestatus.trim().equals("cancel")) {
									flagstatus = 1;
									fail++;
								} else if (tablestatus.trim().equals("check_circle")) {
									pass++;
								} else
									other++;

								JSONArray data = new JSONArray();
								JSONObject timeStamp = new JSONObject();
								JSONObject details = new JSONObject();
								JSONObject statusData = new JSONObject();
								JSONObject screenshot = new JSONObject();

								if (tablestep.startsWith("Navigated to ")) {
									String screenShotUrl = GlobalConstants.JENKINS_URL + ":"
											+ GlobalConstants.JENKINS_PORT + "/jenkins/job/" + jobName + "/ws/"
											+ nextBuildNumber + "/";
									screenShotUrl += "Screenshots/" + title + "/" + name.text().trim() + "/"
											+ tablestep.replaceAll("Navigated to", "").trim() + ".png";
									screenshot.put("screenshot", screenShotUrl);

								} else if (tablestatus.trim().equals("cancel")) {
									String screenShotUrl = GlobalConstants.JENKINS_URL + ":"
											+ GlobalConstants.JENKINS_PORT + "/jenkins/job/" + jobName + "/ws/"
											+ nextBuildNumber + "/";
									screenShotUrl += "Screenshots/" + title + "/" + name.text().trim() + "/Error.png";
									screenshot.put("screenshot", screenShotUrl);
								} else {
									screenshot.put("screenshot", "");
								}

								System.out.println("3.table status is " + tablestatus + " tablestep is " + tablestep);
								timeStamp.put("Timestamp", tabletime);
								details.put("Details", tablestep);
								if (tablestatus.trim().equals("cancel")) {
									statusData.put("status", "fail");
								} else if (tablestatus.trim().equals("check_circle")) {
									statusData.put("status", "pass");
								} else {
									statusData.put("status", "others");
								}

								data.add(screenshot);
								data.add(timeStamp);
								data.add(details);
								data.add(statusData);
								componentIndJSON.add(data);
							}

							try {
								Thread.sleep(1000);
								comp.put("componentsArray", componentIndJSON);
								System.out.println("!132.........." + componentIndJSON);

								if (flagstatus == 1)
									comp.put("overallStatus", "fail");
								else
									comp.put("overallStatus", "pass");

								componentAllJSON.add(comp);
							} catch (Exception e2) {
								e2.printStackTrace();
							}
						}
						tablejson.put("total_test_cases", fail + pass + other);
						tablejson.put("failed", fail);
						tablejson.put("passed", pass);

						tablejson.put("components", componentAllJSON);
						tabularAllJSON.add(tablejson);
						System.out.println("!32.........." + tabularAllJSON);
					}
					toolRe.put("total_execution_time", totexcetime + "Min");
					System.out.println("!2.........." + toolRe);
					toolRe.put("tabular_data", tabularAllJSON);
					toolReport.add(toolRe);
					// tool.put("ToolReport", toolReport);
				} catch (Exception e) {
					e.printStackTrace();
				}

			}
			tool.put("ToolReport", toolReport);
		}

		System.out.println("1..............." + tool);
		System.out.println("2..............." + toolReport);

		return toolReport;

	}

}
