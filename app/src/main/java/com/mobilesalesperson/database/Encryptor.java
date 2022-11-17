package com.mobilesalesperson.database;

import java.io.BufferedInputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.UnsupportedEncodingException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.json.JSONException;
import org.json.JSONObject;
import org.ksoap2.serialization.PropertyInfo;
import org.ksoap2.serialization.SoapObject;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;

import android.app.Activity;
import android.os.Environment;
import android.util.Base64;
import android.util.Log;

import com.mobilesalesperson.model.HhCompany;
import com.mobilesalesperson.model.HhContractPrice01;
import com.mobilesalesperson.model.HhConversionFactor01;
import com.mobilesalesperson.model.HhCustomer01;
import com.mobilesalesperson.model.HhCustomerGroup01;
import com.mobilesalesperson.model.HhItem01;
import com.mobilesalesperson.model.HhItemPriceTax01;
import com.mobilesalesperson.model.HhItemTax01;
import com.mobilesalesperson.model.HhLicense;
import com.mobilesalesperson.model.HhLocMast01;
import com.mobilesalesperson.model.HhManager;
import com.mobilesalesperson.model.HhMultiUOM01;
import com.mobilesalesperson.model.HhPayment01;
import com.mobilesalesperson.model.HhPrice01;
import com.mobilesalesperson.model.HhPriceListMaster01;
import com.mobilesalesperson.model.HhReceiptType01;
import com.mobilesalesperson.model.HhRoute01;
import com.mobilesalesperson.model.HhSetting;
import com.mobilesalesperson.model.HhShipTo01;
import com.mobilesalesperson.model.HhShipVia01;
import com.mobilesalesperson.model.HhTaxClass01;
import com.mobilesalesperson.model.HhTaxGroup01;
import com.mobilesalesperson.model.HhTaxRate01;
import com.mobilesalesperson.model.HhTerms01;
import com.mobilesalesperson.model.HhUPC01;
import com.mobilesalesperson.model.Manf_Number01;
import com.mobilesalesperson.model.Mspdb;
import com.mobilesalesperson.util.LogfileCreator;
import com.mobilesalesperson.util.Supporter;

public class Encryptor {

	private StringBuilder resultString;
	private MspDBHelper dbhelper;
	private HhSetting setting;
	private Supporter supporter;
	private static int j = 0, len;
	private static String result;
	private static String[] availFiles = null;
	
	public String loadData(MspDBHelper dbhelpher, String salesPersonCode,
			String[] availFiles, Activity activity) {
		String result = "";
		resultString = new StringBuilder();
		this.dbhelper = dbhelpher;
		dbhelper.openReadableDatabase();
		setting = dbhelper.getSettingData();
		dbhelper.closeDatabase();
		supporter = new Supporter(activity, dbhelper);

		boolean isSdCardPresent = isSdPresent();

		if (isSdCardPresent) {

			File root_path = Environment.getExternalStorageDirectory();

			File folder_path = new File(root_path.getAbsoluteFile() + "/"
					+ "Android/AMSP/" + salesPersonCode);

			dbhelpher.getWritableDatabase(); // to set db as writable
			Log.i("Writable DB Open", "Writable Database Opened.");
			dbhelpher.mBeginTransaction(); // setting the transaction begin.
			Log.i("Transaction started", "Transaction successfully started.");

			if (folder_path.exists()) {

				for (int i = 0; i < availFiles.length; i++) {

					InputStream fis;
					String strCompCode = "";
					try {

						String fileName = availFiles[i];

						// to split file name to load any company data
						String[] parts = fileName
								.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
						String strfileName = parts[0];

						if (parts.length > 1) {
							String tempStr = parts[1];
							strCompCode = tempStr.split("_")[0];
						} else {
							System.out.println("no data");
						}

						// below code comment by TISN its only for single
						// company
						/*
						 * if (fileName.startsWith("hhCustomer")) {
						 * supporter.setTempCompanyNo(parts[1]); }
						 */

						// to set file path
						String strPath = folder_path.getAbsoluteFile() + "/"
								+ fileName + ".txt";

						String service = setting.getHhSetting_datasyncservice();

						String isOverwrite = supporter.getOverwriteDB();

						if (service.equals("Wired Sync")
								|| isOverwrite.equals("yes")) {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName;
							String[] fileNms = strfileName.split(".txt");
							strfileName = fileNms[0];

						} else {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName + ".txt";
						}

						File file = new File(strPath);

						fis = new BufferedInputStream(new FileInputStream(file));

						char current;
						char org;
						Log.i("", " ");
						Log.i("", "////////////" + fileName
								+ "///////////////////////////////////");
						while (fis.available() > 0) {
							current = (char) fis.read(); // to read character
															// from file
							int ascii = (int) current; // to get ascii for the
														// character
							org = (char) (ascii - 2); // to get the actual
														// character

							if (ascii == 10) { // for ascii 10 string builder
												// value is added to appropriate
												// table

								String strResult = resultString.toString();
								Log.i(fileName + ": ", strResult);

								if (strfileName.equals("hhCompany")) {
									dbhelpher.addCompanyRecord(strResult);
								} else if (strfileName
										.equals("HHContractprice")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher
											.addContractPriceRecord(resultString
													.toString());
								} else if (strfileName
										.equals("hhConversionFactor")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher
											.addConversionFactorRecord(resultString
													.toString());
								} else if (strfileName.equals("hhCustomer")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addCustomerRecord(resultString
											.toString());
								} else if (strfileName
										.equals("hhCustomerGroup")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addGroupCodeRecord(resultString
											.toString());
								} else if (strfileName.equals("hhItem")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addItemRecord(resultString
											.toString());
								} else if (strfileName.equals("hhItemPriceTax")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addItmPrcTaxRecord(resultString
											.toString());
								} else if (strfileName.equals("hhItemTax")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addItemTaxRecord(resultString
											.toString());
								} else if (strfileName.equals("hhLocMast")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addLocMasterRecord(resultString
											.toString());
								} else if (strfileName.equals("hhManager")) {
									dbhelpher.addManagerRecord(strResult);
								} else if (strfileName.equals("hhMultiUOM")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addMultiUOMRecord(resultString
											.toString());
								} else if (strfileName.equals("hhPayment")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addPaymentDataRecord(resultString
											.toString());
								} else if (strfileName.equals("hhPrice")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addPriceRecord(resultString
											.toString());
								} else if (strfileName
										.equals("hhPriceListMaster")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher
											.addPriceListMasterRecord(resultString
													.toString());
								} else if (strfileName.equals("HHReceiptType")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addReceiptTypeRecord(resultString
											.toString());
								} else if (strfileName.equals("hhRoute")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addRouteRecord(resultString
											.toString());
								} else if (strfileName.equals("hhShipTo")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addShipToRecord(resultString
											.toString());
								} else if (strfileName.equals("hhShipVia")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addShipViaRecord(resultString
											.toString());
								} else if (strfileName.equals("hhTaxClass")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addTaxClassRecord(resultString
											.toString());
								} else if (strfileName.equals("hhTaxGroup")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addTaxGroupRecord(resultString
											.toString());
								} else if (strfileName.equals("hhTaxRate")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addTaxRateRecord(resultString
											.toString());
								} else if (strfileName.equals("hhTerms")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addTermsRecord(resultString
											.toString());
								} else if (strfileName.equals("hhUPC")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher.addUPCRecord(resultString
											.toString());
								} else if (strfileName.equals("manf_number")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									dbhelpher
											.addManufactureNumberDataRecord(resultString
													.toString());
								} else if (strfileName.equals("MSPDB")) {
									checkMsbDbDataForInsertorUpdate(strResult);
								} else if (strfileName.equals("hhlicense")) {
									licenseDataLoad(strResult);
								}

								resultString.setLength(0);

							} else if (ascii != 13) { // other than the ascii
														// 13, the character are
														// appended with string
														// builder
								resultString.append(org);
							}

						}
						fis.close();
						result = "success";

					} catch (FileNotFoundException e) {
						dbhelpher.mEndTransaction();
						Log.i("Transaction Failure",
								"Transaction failure because of file not found.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					} catch (IOException e) {
						dbhelpher.mEndTransaction();
						Log.i("Transaction Failure",
								"Transaction failure because of input/output error.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					}

				}

				dbhelpher.mSetTransactionSuccess(); // setting the transaction
													// successfull.
				Log.i("Transaction success", "Transaction success.");
				dbhelpher.mEndTransaction();
				Log.i("Transaction success", "Transaction end.");
				dbhelpher.closeDatabase();
				Log.i("DB closed", "Database closed successfully.");

			} else {
				result = "salesPerson not valid";
			}

		} else {
			result = "nosd";
		}

		return result;

	}

	// this method used for import inventory items
	// developed by TISN 16-Jul-2013
	public String loadInventoryData(MspDBHelper dbhelpher,
			String salesPersonCode, String cmpnyNo, String[] availFiles,
			Activity activity) {
		String result = "";
		resultString = new StringBuilder();
		this.dbhelper = dbhelpher;
		dbhelper.openReadableDatabase();
		setting = dbhelper.getSettingData();
		dbhelper.closeDatabase();
		supporter = new Supporter(activity, dbhelper);

		boolean isSdCardPresent = isSdPresent();

		if (isSdCardPresent) {

			File root_path = Environment.getExternalStorageDirectory();

			File folder_path = new File(root_path.getAbsoluteFile() + "/"
					+ "Android/AMSP/" + salesPersonCode);

			if (folder_path.exists()) {

				for (int i = 0; i < availFiles.length; i++) {

					InputStream fis;
					String strCompCode = "";
					try {

						String fileName = availFiles[i];

						// to split file name to load any company data
						String[] parts = fileName
								.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
						String strfileName = parts[0];

						if (parts.length > 1) {
							String tempStr = parts[1];
							strCompCode = tempStr.split("_")[0];
						} else {
							System.out.println("no data");
						}

						// to set file path
						String strPath = folder_path.getAbsoluteFile() + "/"
								+ fileName + ".txt";

						String service = setting.getHhSetting_datasyncservice();

						String isOverwrite = supporter.getOverwriteDB();

						if (service.equals("Wired Sync")
								|| isOverwrite.equals("yes")) {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName;
							String[] fileNms = strfileName.split(".txt");
							strfileName = fileNms[0];

						} else {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName + ".txt";
						}

						File file = new File(strPath);

						fis = new BufferedInputStream(new FileInputStream(file));

						char current;
						char org;
						Log.i("", " ");
						Log.i("", "////////////" + fileName
								+ "///////////////////////////////////");
						while (fis.available() > 0) {
							current = (char) fis.read();
							int ascii = (int) current;
							org = (char) (ascii - 2);

							if (ascii == 10) {

								String strResult = resultString.toString();
								Log.i(fileName + ": ", strResult);

								if (strfileName.equals("hhTransfers")) {
									// dbhelpher.addCompanyRecord(strResult);
									String[] splitStr = strResult.split(",");
									String itmNum = splitStr[0].replaceAll("'",
											"");
									String itmQty = splitStr[1].replaceAll("'",
											"");
									String itmLoc = splitStr[2].replaceAll("'",
											"");

									System.out.println("Item Number : "
											+ itmNum);
									System.out.println("Item Qty : " + itmQty);
									System.out.println("Item Location : "
											+ itmLoc);

									dbhelpher.getWritableDatabase(); // to set
																		// db as
																		// writable
									Log.i("Writable DB Open",
											"Writable Database Opened.");
									dbhelpher.mBeginTransaction(); // setting
																	// the
																	// transaction
																	// begin.
									Log.i("Import Inventory started",
											"Import Inventory successfully started.");

									Double mOldQty = dbhelpher.getItemQty(
											itmNum, itmLoc, cmpnyNo);
									Double mUpdateQty = mOldQty
											+ Double.parseDouble(itmQty);
									System.out.println("Old Item Quantity : "
											+ mOldQty);
									System.out
											.println("Updated Item Quantity : "
													+ mUpdateQty);

									dbhelpher.updateInventory(itmNum, itmLoc,
											cmpnyNo, mUpdateQty);

									dbhelpher.mSetTransactionSuccess(); // setting the transaction
													// successfull.
				Log.i("Import Inventory success", "Import Inventory success.");
				dbhelpher.mEndTransaction();
				Log.i("Import Inventory success", "Import Inventory end.");
				dbhelpher.closeDatabase();
				Log.i("DB closed", "Database closed successfully.");
								}

								resultString.setLength(0);

							} else if (ascii != 13) {
								resultString.append(org);
							}

						}
						fis.close();
						result = "success";

					} catch (FileNotFoundException e) {
						dbhelpher.mEndTransaction();
						Log.i("Import Inventory Failure",
								"Import Inventory failure because of file not found.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					} catch (IOException e) {
						dbhelpher.mEndTransaction();
						Log.i("Import Inventory Failure",
								"Import Inventory failure because of input/output error.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					}

				}

				

			} else {
				result = "salesPerson not valid";
			}

		} else {
			result = "nosd";
		}

		return result;

	}

	// this method used for incremental transfer data
	// developed by TISN 25-Jul-2013
	public String loadIncremetalData(MspDBHelper dbhelpher,
			String salesPersonCode, String[] availFiles, Activity activity) {
		String result = "";
		resultString = new StringBuilder();
		this.dbhelper = dbhelpher;
		dbhelper.openReadableDatabase();
		setting = dbhelper.getSettingData();
		dbhelper.closeDatabase();
		supporter = new Supporter(activity, dbhelper);

		boolean isSdCardPresent = isSdPresent();

		if (isSdCardPresent) {

			File root_path = Environment.getExternalStorageDirectory();

			File folder_path = new File(root_path.getAbsoluteFile() + "/"
					+ "Android/AMSP/" + salesPersonCode);

			dbhelpher.getWritableDatabase(); // to set db as writable
			Log.i("Writable DB Open", "Writable Database Opened.");
			dbhelpher.mBeginTransaction(); // setting the transaction begin.
			Log.i("Transaction started", "Transaction successfully started.");

			if (folder_path.exists()) {

				for (int i = 0; i < availFiles.length; i++) {

					InputStream fis;
					String strCompCode = "";
					try {

						String fileName = availFiles[i];

						// to split file name to load any company data
						String[] parts = fileName
								.split("(?<=\\d)(?=\\D)|(?<=\\D)(?=\\d)");
						String strfileName = parts[0];

						if (parts.length > 1) {
							String tempStr = parts[1];
							strCompCode = tempStr.split("_")[0];
						} else {
							System.out.println("no data");
						}

						// to set file path
						String strPath = folder_path.getAbsoluteFile() + "/"
								+ fileName + ".txt";

						String service = setting.getHhSetting_datasyncservice();

						String isOverwrite = supporter.getOverwriteDB();

						if (service.equals("Wired Sync")
								|| isOverwrite.equals("yes")) {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName;
							String[] fileNms = strfileName.split(".txt");
							strfileName = fileNms[0];

						} else {
							strPath = folder_path.getAbsoluteFile() + "/"
									+ fileName + ".txt";
						}

						File file = new File(strPath);

						fis = new BufferedInputStream(new FileInputStream(file));

						char current;
						char org;
						Log.i("", " ");
						Log.i("", "////////////" + fileName
								+ "///////////////////////////////////");
						while (fis.available() > 0) {
							current = (char) fis.read(); // to read character
															// from file
							int ascii = (int) current; // to get ascii for the
														// character
							org = (char) (ascii - 2); // to get the actual
														// character

							if (ascii == 10) { // for ascii 10 string builder
												// value is added to appropriate
												// table

								String strResult = resultString.toString();
								Log.i(fileName + ": ", strResult);

								if (strfileName.equals("hhCompany")) {
									checkCompanyDataForInsertorUpdate(strResult);
								} else if (strfileName
										.equals("HHContractprice")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkContractPriceDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName
										.equals("hhConversionFactor")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkConversionFactorDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhCustomer")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkCustomerDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName
										.equals("hhCustomerGroup")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkCustomerGroupDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhItem")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkItemDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhItemPriceTax")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkItemPriceTaxDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhItemTax")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkItemTaxDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhLocMast")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkLocationDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhManager")) {
									checkManagerDataForInsertorUpdate(strResult);
								} else if (strfileName.equals("hhMultiUOM")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkMultiUOMDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhPayment")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkPaymentDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhPrice")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkPriceDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName
										.equals("hhPriceListMaster")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkPriceListMasterDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("HHReceiptType")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkReceiptTypeDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhRoute")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkRouteDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhShipTo")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkShipToDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhShipVia")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkShipViaDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhTaxClass")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkTaxClassDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhTaxGroup")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkTaxGroupDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhTaxRate")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkTaxRateDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhTerms")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkTermsDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("hhUPC")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkUPCDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("manf_number")) {
									resultString = resultString.append(",'"
											+ strCompCode + "'");
									checkManfNumberDataForInsertorUpdate(resultString
											.toString());
								} else if (strfileName.equals("MSPDB")) {
									checkMsbDbDataForInsertorUpdate(strResult);
								} else if (strfileName.equals("hhlicense")) {
									licenseDataLoad(strResult);
								}

								resultString.setLength(0);

							} else if (ascii != 13) { // other than the ascii
														// 13, the character are
														// appended with string
														// builder
								resultString.append(org);
							}

						}
						fis.close();
						result = "success";

					} catch (FileNotFoundException e) {
						dbhelpher.mEndTransaction();
						Log.i("Transaction Failure",
								"Transaction failure because of file not found.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					} catch (IOException e) {
						dbhelpher.mEndTransaction();
						Log.i("Transaction Failure",
								"Transaction failure because of input/output error.");
						dbhelpher.closeDatabase();
						Log.i("DB closed", "Database closed successfully.");
						e.printStackTrace();
						result = "error";
					}

				}

				dbhelpher.mSetTransactionSuccess(); // setting the transaction
													// successfull.
				Log.i("Transaction success", "Transaction success.");
				dbhelpher.mEndTransaction();
				Log.i("Transaction success", "Transaction end.");
				dbhelpher.closeDatabase();
				Log.i("DB closed", "Database closed successfully.");

			} else {
				result = "salesPerson not valid";
			}

		} else {
			result = "nosd";
		}

		return result;

	}

	public static List<String[]> parseOutput(SoapObject resultString,
			String sCode) {
		List<String[]> resultList = new ArrayList<String[]>();
		String[] resultArray = new String[1];
		String[] availFiles = null;
		String result = "success";

		int elementCount = resultString.getPropertyCount();

		for (int i = 0; i < elementCount; i++) {
			PropertyInfo pi = new PropertyInfo();
			SoapObject nestedSO = (SoapObject) resultString.getProperty(i);

			int nestedElementCount = nestedSO.getPropertyCount();
			availFiles = new String[nestedElementCount];
			for (int j = 0; j < nestedElementCount; j++) {
				nestedSO.getPropertyInfo(j, pi);
				String fileName = pi.getName();
				String value = nestedSO.getProperty(j).toString();

				result = createFile(sCode, fileName, value);
				availFiles[j] = fileName;
				Log.i("File Names: ", fileName);
			}
		}

		resultArray[0] = result;
		resultList.add(resultArray);
		resultList.add(availFiles);

		return resultList;
	}
	public static List<String[]> parseEmailOutput(String path, String sCode) {
		List<String[]> resultLst = new ArrayList<String[]>();

		// parse XML file -> XML document will be build
		Document doc = parseFile(path);
		// get root node of xml tree structure
		Node root = doc.getDocumentElement();
		// write node and its child nodes into System.out

		System.out.println("Statement of XML document...");

		resultLst = writeDocumentToOutput(root, 0, sCode);

		System.out.println("... end of statement");
		j = 0;
		return resultLst;
	}

	private static Document parseFile(String fileName) {

		System.out.println("Parsing XML file... " + fileName);
		DocumentBuilder docBuilder;
		Document doc = null;
		DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory
				.newInstance();
		docBuilderFactory.setIgnoringElementContentWhitespace(true);
		try {
			docBuilder = docBuilderFactory.newDocumentBuilder();
		} catch (ParserConfigurationException e) {
			System.out.println("Wrong parser configuration: " + e.getMessage());
			return null;
		}
		File sourceFile = new File(fileName);
		try {
			doc = docBuilder.parse(sourceFile);
		} catch (SAXException e) {
			System.out.println("Wrong XML file structure: " + e.getMessage());
			return null;
		} catch (IOException e) {
			System.out.println("Could not read source file: " + e.getMessage());
		}
		System.out.println("XML file parsed");
		return doc;
	
	}

	private static List<String[]> writeDocumentToOutput(Node node, int indent,
			String sCode) {
		// get element name

		String[] resultArray = new String[1];
		// String result = "success";//remove this at top since recursive

		List<String[]> resultList = new ArrayList<String[]>();

		NodeList children = node.getChildNodes();
		if (j == 0) {
			len = (children.getLength() / 2 + 2);
			availFiles = new String[len];
		}

		if (j < len) {

			// get element name and value
			String fileName = node.getNodeName();
			String value = getElementValue(node);

			// Since Always XML starts with <MSPData> so hardcoded
			if (!"MSPData".equals(fileName)) {
				result = createFile(sCode, fileName, value);

				availFiles[j - 1] = fileName;
				j++;

			} else {
				j++;
			}

			for (int i = 0; i < children.getLength(); i++) {
				Node child = children.item(i);
				if (child.getNodeType() == Node.ELEMENT_NODE) {
					writeDocumentToOutput(child, indent + 2, sCode);
				}

			}
		}

		if (resultList.isEmpty() && j == len - 1) {

			String av[] = clean(availFiles);
			resultArray[0] = result;
			resultList.add(resultArray);
			resultList.add(av);

		}

		return resultList;

	}
	public static String[] clean(final String[] v) {
		List<String> list = new ArrayList<String>(Arrays.asList(v));
		list.removeAll(Collections.singleton(null));
		return list.toArray(new String[list.size()]);
	}
	public final static String getElementValue(Node elem) {
		Node kid;
		if (elem != null) {
			if (elem.hasChildNodes()) {
				for (kid = elem.getFirstChild(); kid != null; kid = kid
						.getNextSibling()) {
					if (kid.getNodeType() == Node.TEXT_NODE) {
						return kid.getNodeValue();
					}
				}
			}
		}
		return "";
	}

	// to check license details
	public String checkLicenceValidity() {

		String result = "0";

		String stringToDecode = readLicenceData();
		result = "full license";

		/*if (!stringToDecode.equals("nodata")) {

			String decodedData = decodeData(stringToDecode);

			JSONObject jsonObj = null;
			String installDate = "";
			String noOfDays = "";
			try {
				jsonObj = new JSONObject(decodedData);
				installDate = jsonObj.getString("installDate");
				noOfDays = jsonObj.getString("noOfDays");
			} catch (JSONException e) {
				e.printStackTrace();
				LogfileCreator.appendLog(e.getMessage());
			}

			long totalDays = Long.parseLong(noOfDays);

			if (totalDays == 0) {
				result = "full license";
			} else {

				SimpleDateFormat dateFormater = new SimpleDateFormat(
						"dd-MM-yyyy");
				Date today = new Date();
				long daysCompleted = checkNumberOfDays(installDate,
						dateFormater.format(today));

				if ((daysCompleted >= 0) && (daysCompleted < totalDays)) {
					long remainingDays = totalDays - daysCompleted;
					result = remainingDays + "";
				} else {
					result = "expired";
				}

			}

		} else {
			result = "nodata";
		}*/

		return result;
	}

	private String readLicenceData() {

		String result = "nodata";

		File common_path = createCommonPath();

		File file = new File(common_path, "licence.txt");

		if (file.exists()) {

			BufferedReader br = null;

			try {
				br = new BufferedReader(new FileReader(file));

				String line = br.readLine();

				if (line == null) {
					result = "nodata";
				} else {
					result = line;
				}

			} catch (FileNotFoundException e) {
				e.printStackTrace();
				LogfileCreator.appendLog(e.getMessage());
			} catch (IOException e) {
				e.printStackTrace();
				LogfileCreator.appendLog(e.getMessage());
			}

		} else {
			result = "nodata";
		}

		return result;
	}

	private String encodeData(String jsonString) {

		byte[] jsonByte = null;
		try {
			jsonByte = jsonString.getBytes("UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			LogfileCreator.appendLog(e.getMessage());
		}

		String encodedString = Base64.encodeToString(jsonByte, Base64.DEFAULT);

		return encodedString;
	}

	private String decodeData(String encodedString) {

		byte[] decodedByte = Base64.decode(encodedString, Base64.DEFAULT);

		String decodedString = null;
		try {
			decodedString = new String(decodedByte, "UTF-8");
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
			LogfileCreator.appendLog(e.getMessage());
		}

		return decodedString;
	}

	public void writeLicenceData(HhLicense license) {

		String jsonString = "{\"installDate\":\"" + license.getInstallDate()
				+ "\",\"noOfDays\":\"" + license.getNoOfDays() + "\"}";

		File common_path = createCommonPath();

		String encodedLicenseString = encodeData(jsonString);

		try {
			File file = new File(common_path, "licence.txt");

			if (!file.exists()) {
				file.createNewFile();
			}

			FileOutputStream fos = new FileOutputStream(file);
			fos.write(encodedLicenseString.getBytes());
			fos.close();
			Log.i("File Creation", "Licence file created successfully");

		} catch (IOException e) {
			e.printStackTrace();
			Log.e("Failure in licence file creation",
					"Licence file creation failure");
			LogfileCreator.appendLog(e.getMessage());
		}

	}

	public File createCommonPath() {
		File root_path = Environment.getExternalStorageDirectory();

		File msp_common_path = new File(root_path.getAbsoluteFile() + "/"
				+ "Android/AMSP");

		if (!msp_common_path.exists()) {
			msp_common_path.mkdirs();
		}

		return msp_common_path;
	}

	private long checkNumberOfDays(String strDateStart, String strDateNow) {

		java.text.DateFormat df = new java.text.SimpleDateFormat("dd-MM-yyyy");
		java.util.Date dateStart = null;
		java.util.Date dateNow = null;
		try {
			dateStart = df.parse(strDateStart);
			dateNow = df.parse(strDateNow);
		} catch (ParseException e) {
			e.printStackTrace();
		}

		long diff = dateNow.getTime() - dateStart.getTime();

		long millisPerDay = 1000 * 60 * 60 * 24;

		long day = diff / millisPerDay;

		return day;
	}

	public static String createFile(String rootFolder, String fileName,
			String value) {
		String result = "success";
		boolean isSdCardPresent = isSdPresent();

		if (isSdCardPresent) {

			File root_path = Environment.getExternalStorageDirectory();
			File folder_path = new File(root_path.getAbsoluteFile() + "/"
					+ "Android/AMSP/" + rootFolder);

			if (!folder_path.exists()) {
				folder_path.mkdir();
			}

			try {
				File file = new File(folder_path, fileName + ".txt");

				if (!file.exists()) {
					file.createNewFile();
				}

				if (!value.equals("anyType{}")) {
					FileOutputStream fos = new FileOutputStream(file);
					fos.write(value.getBytes());
					fos.close();
					Log.i("File Creation", fileName + " created successfully");
				}
			} catch (IOException e) {
				e.printStackTrace();
				result = "error in file creation";
				Log.e("Failure in file creation", fileName
						+ " creation failure");
			}

		} else {
			result = "nosd";
		}

		return result;

	}

	public List<String> getFileNames(File myDir) {

		List<String> fileNames = new ArrayList<String>();
		for (File f : myDir.listFiles()) {
			if (f.isFile()) {
				String name = f.getName();

				String sName = name.toLowerCase();

				if (!sName.equals("msp") && !sName.equals("msp.xml")) {
					fileNames.add(name);
				}
			}

		}

		return fileNames;
	}

	public void moveFile(String sourceName, String destinationName) {
		File root_path = Environment.getExternalStorageDirectory();

		File folder_path = new File(root_path.getAbsoluteFile() + "/"
				+ "Android/AMSP/" + sourceName);

		File move_path = new File(root_path.getAbsoluteFile() + "/"
				+ "Android/AMSP/" + destinationName);

		if (!move_path.exists()) {
			move_path.mkdir();
		}
		// folder moving...
		if (folder_path.renameTo(new File(move_path + "/"
				+ folder_path.getName()))) {

			Log.i("Folder moved", "Folder moved successfully...");

			File file = new File(move_path, sourceName);

			SimpleDateFormat sdf = new SimpleDateFormat("yyyy.MM.dd_HH.mm.ss");

			String currentDateandTime = sdf.format(new Date());

			if (file.exists()) {
				String newName = move_path + "/" + sourceName + "_"
						+ currentDateandTime;
				File newfile = new File(newName);
				file.renameTo(newfile); // here to BB folder name is
										// renamed...
			} else {
				Log.e("Error", sourceName + "Folder is not moved proberly...");
			}

		} else {
			Log.e("Error", "Failed to move folder...");
		}
	}

	public void deleteFileFolder(String dirName) {
		File root_path = Environment.getExternalStorageDirectory();

		File folder_path = new File(root_path.getAbsoluteFile() + "/"
				+ "Android/AMSP/" + dirName);

		if (folder_path.exists()) {
			deleteAllFile(folder_path);
			folder_path.delete();
		}
	}

	public void deleteAllFile(File folder) {
		for (File file : folder.listFiles()) {
			file.delete();
		}
	}

	public static boolean isSdPresent() {

		return android.os.Environment.getExternalStorageState().equals(
				android.os.Environment.MEDIA_MOUNTED);

	}

	// Insert or Update company data when doing import or Incremental Transfer.
	private void checkCompanyDataForInsertorUpdate(String record) {

		String[] strarray = record.split(",");
		String companyNo = strarray[0].replace("'", "");
		String companyName = strarray[1].replace("'", "");
		String address = strarray[2].replace("'", "");
		String city = strarray[3].replace("'", "");
		String state = strarray[4].replace("'", "");
		String zip = strarray[5].replace("'", "");
		String country = strarray[6].replace("'", "");
		String phone = strarray[7].replace("'", "");
		String contact = strarray[8].replace("'", "");
		int isstockinguom = Integer.parseInt(strarray[9].replace("'", ""));
		String tinno = strarray[10].replace("'", "");

		HhCompany company = dbhelper.getCompanyData(companyNo);
		if (company != null) {
			HhCompany companyRecord = new HhCompany();

			companyRecord.setCompany_number(companyNo);
			companyRecord.setCompany_name(companyName);
			companyRecord.setCompany_address(address);
			companyRecord.setCompany_city(city);
			companyRecord.setCompany_state(state);
			companyRecord.setCompany_zip(zip);
			companyRecord.setCompany_country(country);
			companyRecord.setCompany_phone(phone);
			companyRecord.setCompany_contact(contact);
			companyRecord.setCompany_isstockinguom(isstockinguom);
			companyRecord.setCompany_tinno(tinno);

			dbhelper.updateCompany(companyRecord, companyNo);
		} else {
			dbhelper.addCompanyRecord(record);
		}
	}

	// Insert or Update contractPrice data when doing import or Incremental
	// Transfer.
	private void checkContractPriceDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");
		String customerNo = strarray[0].replace("'", "");
		int priceby = Integer.parseInt(strarray[1].replace("'", ""));
		String category = strarray[2].replace("'", "");
		String itemNo = strarray[3].replace("'", "");
		String priceListCode = strarray[4].replace("'", "");
		int priceType = Integer.parseInt(strarray[5].replace("'", ""));
		int cusType = Integer.parseInt(strarray[6].replace("'", ""));
		double discPercent = Double.parseDouble(strarray[7].replace("'", ""));
		double discAmt = Double.parseDouble(strarray[8].replace("'", ""));
		int costMethod = Integer.parseInt(strarray[9].replace("'", ""));
		double plusAmt = Double.parseDouble(strarray[10].replace("'", ""));
		double plusPercent = Double.parseDouble(strarray[11].replace("'", ""));
		double fixedPrice = Double.parseDouble(strarray[12].replace("'", ""));
		double calcunitPrice = Double
				.parseDouble(strarray[13].replace("'", ""));
		double useLowest = Double.parseDouble(strarray[14].replace("'", ""));
		String companyCode = strarray[15].replace("'", "");

		HhContractPrice01 contractPrz = dbhelper.getContractPrc(customerNo,
				itemNo, priceListCode, category, companyCode);
		if (contractPrz != null) {
			HhContractPrice01 contPrzRecord = new HhContractPrice01();

			contPrzRecord.setHhContPrc_customernumber(customerNo);
			contPrzRecord.setHhContPrc_prictby(priceby);
			contPrzRecord.setHhContPrc_category(category);
			contPrzRecord.setHhContPrc_itemnumber(itemNo);
			contPrzRecord.setHhContPrc_pricelistcode(priceListCode);
			contPrzRecord.setHhContPrc_pricetype(priceType);
			contPrzRecord.setHhContPrc_customertype(cusType);
			contPrzRecord.setHhContPrc_discpercent(discPercent);
			contPrzRecord.setHhContPrc_discamount(discAmt);
			contPrzRecord.setHhContPrc_costmethod(costMethod);
			contPrzRecord.setHhContPrc_plusamount(plusAmt);
			contPrzRecord.setHhContPrc_pluspercent(plusPercent);
			contPrzRecord.setHhContPrc_fixedprice(fixedPrice);
			contPrzRecord.setHhContPrc_calcunitprice(calcunitPrice);
			contPrzRecord.setHhContPrc_uselowest(useLowest);
			contPrzRecord.setHhContPrc_companycode(companyCode);

			dbhelper.updateContractPrice(contPrzRecord, customerNo, companyCode);
		} else {
			dbhelper.addContractPriceRecord(record);
		}
	}

	// Insert or Update conversionFactor data when doing import or Incremental
	// Transfer.
	private void checkConversionFactorDataForInsertorUpdate(String record) {

		String[] strarray = record.split(",");
		String number = strarray[0].replace("'", "");
		double uomconvfactor = Double.parseDouble(strarray[1].replace("'", ""));
		String uomname = strarray[2].replace("'", "");
		String companyCode = strarray[3].replace("'", "");

		HhConversionFactor01 convFactor = dbhelper.getconversionFactor(number,
				uomname, companyCode);
		if (convFactor != null) {
			HhConversionFactor01 convFactorRecord = new HhConversionFactor01();

			convFactorRecord.setHhConversionFactor_number(number);
			convFactorRecord
					.setHhConversionFactor_uom_conv_factor(uomconvfactor);
			convFactorRecord.setHhConversionFactor_name(uomname);
			convFactorRecord.setHhConversionFactor_companycode(companyCode);

			dbhelper.updateConversionFactor(convFactorRecord, number, uomname,
					companyCode);
		} else {
			dbhelper.addConversionFactorRecord(record);
		}
	}

	// Insert or Update Customer data when doing import or Incremental Transfer.
	private void checkCustomerDataForInsertorUpdate(String record) {

		String[] strarray = record.split(",");

		String number = strarray[0].replace("'", "");
		String name = strarray[1].replace("'", "");
		String address = strarray[2].replace("'", "");
		String city = strarray[3].replace("'", "");
		String state = strarray[4].replace("'", "");
		String zip = strarray[5].replace("'", "");
		String country = strarray[6].replace("'", "");
		String phone1 = strarray[7].replace("'", "");
		String email1 = strarray[8].replace("'", "");
		String website = strarray[9].replace("'", "");
		String contact = strarray[10].replace("'", "");
		String email2 = strarray[11].replace("'", "");
		String groupcode = strarray[12].replace("'", "");
		String type = strarray[13].replace("'", "");
		String terms = strarray[14].replace("'", "");
		double lastbalance = Double.parseDouble(strarray[15].replace("'", ""));
		double creditlimit = Double.parseDouble(strarray[16].replace("'", ""));
		double lastsale = Double.parseDouble(strarray[17].replace("'", ""));
		String taxgroup = strarray[18].replace("'", "");
		String comment = strarray[19].replace("'", "");
		String taxauthority1 = strarray[20].replace("'", "");
		int taxable1 = Integer.parseInt(strarray[21].replace("'", ""));
		String taxstts1 = strarray[22].replace("'", "");
		String taxauthority2 = strarray[23].replace("'", "");
		int taxable2 = Integer.parseInt(strarray[24].replace("'", ""));
		String taxstts2 = strarray[25].replace("'", "");
		String taxauthority3 = strarray[26].replace("'", "");
		int taxable3 = Integer.parseInt(strarray[27].replace("'", ""));
		String taxstts3 = strarray[28].replace("'", "");
		String taxauthority4 = strarray[29].replace("'", "");
		int taxable4 = Integer.parseInt(strarray[30].replace("'", ""));
		String taxstts4 = strarray[31].replace("'", "");
		String taxauthority5 = strarray[32].replace("'", "");
		int taxable5 = Integer.parseInt(strarray[33].replace("'", ""));
		String taxstts5 = strarray[34].replace("'", "");
		int status = Integer.parseInt(strarray[35].replace("'", ""));
		String pricelistcode = strarray[36].replace("'", "");
		String idtaxregi1 = strarray[37].replace("'", "");
		String creditstatus = strarray[38].replace("'", "");
		String companycode = strarray[39].replace("'", "");

		HhCustomer01 customer = dbhelper.getCustomerData(number, companycode);
		if (customer != null) {
			HhCustomer01 customerRecord = new HhCustomer01();

			customerRecord.setHhCustomer_number(number);
			customerRecord.setHhCustomer_name(name);
			customerRecord.setHhCustomer_address(address);
			customerRecord.setHhCustomer_city(city);
			customerRecord.setHhCustomer_state(state);
			customerRecord.setHhCustomer_zip(zip);
			customerRecord.setHhCustomer_country(country);
			customerRecord.setHhCustomer_phone1(phone1);
			customerRecord.setHhCustomer_email1(email1);
			customerRecord.setHhCustomer_website(website);
			customerRecord.setHhCustomer_contact(contact);
			customerRecord.setHhCustomer_email2(email2);
			customerRecord.setHhCustomer_groupcode(groupcode);
			customerRecord.setHhCustomer_type(type);
			customerRecord.setHhCustomer_terms(terms);
			customerRecord.setHhCustomer_lastbalance(lastbalance);
			customerRecord.setHhCustomer_creditlimit(creditlimit);
			customerRecord.setHhCustomer_lastsale(lastsale);
			customerRecord.setHhCustomer_taxgroup(taxgroup);
			customerRecord.setHhCustomer_comment(comment);
			customerRecord.setHhCustomer_taxauthority1(taxauthority1);
			customerRecord.setHhCustomer_taxable1(taxable1);
			customerRecord.setHhCustomer_taxstts1(taxstts1);
			customerRecord.setHhCustomer_taxauthority2(taxauthority2);
			customerRecord.setHhCustomer_taxable2(taxable2);
			customerRecord.setHhCustomer_taxstts2(taxstts2);
			customerRecord.setHhCustomer_taxauthority3(taxauthority3);
			customerRecord.setHhCustomer_taxable3(taxable3);
			customerRecord.setHhCustomer_taxstts3(taxstts3);
			customerRecord.setHhCustomer_taxauthority4(taxauthority4);
			customerRecord.setHhCustomer_taxable4(taxable4);
			customerRecord.setHhCustomer_taxstts4(taxstts4);
			customerRecord.setHhCustomer_taxauthority5(taxauthority5);
			customerRecord.setHhCustomer_taxable5(taxable5);
			customerRecord.setHhCustomer_taxstts5(taxstts5);
			customerRecord.setHhCustomer_status(status);
			customerRecord.setHhCustomer_pricelistcode(pricelistcode);
			customerRecord.setHhCustomer_idtaxregi1(idtaxregi1);
			customerRecord.setHhCustomer_creditstatus(creditstatus);
			customerRecord.setHhCustomer_companycode(companycode);

			dbhelper.updateCustomer(customerRecord, companycode);
		} else {
			System.out.println(record);
			dbhelper.addCustomerRecord(record);
		}
	}

	// Insert or Update customerGroupCode data when doing import or Incremental
	// Transfer.
	private void checkCustomerGroupDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");
		String groupCode = strarray[0].replace("'", "");
		String desciption = strarray[1].replace("'", "");
		String terms = strarray[2].replace("'", "");
		String companyCode = strarray[3].replace("'", "");

		HhCustomerGroup01 custGroup = dbhelper.getCustomerGroupCode(groupCode,
				terms, companyCode);
		if (custGroup != null) {
			HhCustomerGroup01 custGroupRecord = new HhCustomerGroup01();

			custGroupRecord.setHhCustomerGroup_group_code(groupCode);
			custGroupRecord.setHhCustomerGroup_description(desciption);
			custGroupRecord.setHhCustomerGroup_terms(terms);
			custGroupRecord.setHhCustomerGroup_companycode(companyCode);

			dbhelper.updateGroupRecord(custGroupRecord, groupCode, terms,
					companyCode);
		} else {
			dbhelper.addGroupCodeRecord(record);
		}
	}

	// Insert or Update Item data when doing import or Incremental Transfer.
	private void checkItemDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String number = strarray[0].replace("'", "");
		String description = strarray[1].replace("'", "");
		String category = strarray[2].replace("'", "");
		String locid = strarray[3].replace("'", "");
		double qtyonhand = Double.parseDouble(strarray[4].replace("'", ""));
		String currency = strarray[5].replace("'", "");
		String pricelistcode = strarray[6].replace("'", "");
		String altitem = strarray[7].replace("'", "");
		String altitmedesc = strarray[8].replace("'", "");
		String costingmethod = strarray[9].replace("'", "");
		double standardcost = Double.parseDouble(strarray[10].replace("'", ""));
		double mostrecentcost = Double.parseDouble(strarray[11]
				.replace("'", ""));
		double averagecost = Double.parseDouble(strarray[12].replace("'", ""));
		double lastunitcost = Double.parseDouble(strarray[13].replace("'", ""));
		double cost1 = Double.parseDouble(strarray[14].replace("'", ""));
		double cost2 = Double.parseDouble(strarray[15].replace("'", ""));
		String stockinguom = strarray[16].replace("'", "");
		double stockingconvfactor = Double.parseDouble(strarray[17].replace(
				"'", ""));
		String locuom = strarray[18].replace("'", "");
		double locconvfactor = Double
				.parseDouble(strarray[19].replace("'", ""));
		String cortondisc = strarray[20].replace("'", "");
		double unitwgt = Double.parseDouble(strarray[21].replace("'", ""));
		// int stockitem = Integer.parseInt(strarray[22].replace("'", ""));
		String companycode = strarray[22].replace("'", "");

		HhItem01 item = dbhelper.getItemData(number, locid, companycode);
		if (item != null) {
			HhItem01 itemRecord = new HhItem01();

			itemRecord.setHhItem_number(number);
			itemRecord.setHhItem_description(description);
			itemRecord.setHhItem_category(category);
			itemRecord.setHhItem_loc_id(locid);
			itemRecord.setHhItem_qty_on_hand(qtyonhand);
			itemRecord.setHhItem_currency(currency);
			itemRecord.setHhItem_price_list_code(pricelistcode);
			itemRecord.setHhItem_alt_itm(altitem);
			itemRecord.setHhItem_alt_itm_desc(altitmedesc);
			itemRecord.setHhItem_costing_method(costingmethod);
			itemRecord.setHhItem_standard_cost(standardcost);
			itemRecord.setHhItem_most_recent_cost(mostrecentcost);
			itemRecord.setHhItem_avg_cost(averagecost);
			itemRecord.setHhItem_last_unit_cost(lastunitcost);
			itemRecord.setHhItem_cost1(cost1);
			itemRecord.setHhItem_cost2(cost2);
			itemRecord.setHhItem_stocking_uom(stockinguom);
			itemRecord.setHhItem_stocking_conv_factor(stockingconvfactor);
			itemRecord.setHhItem_loc_uom(locuom);
			itemRecord.setHhItem_location_conv_factor(locconvfactor);
			itemRecord.setHhItem_cart_on_disc(cortondisc);
			itemRecord.setHhItem_unitwgt(unitwgt);
			// itemRecord.setHhItem_stockitem(stockitem);
			itemRecord.setHhItem_companycode(companycode);

			dbhelper.updateItemRecord(itemRecord, number, locid, companycode);
		} else {
			dbhelper.addItemRecord(record);
		}
	}

	// Insert or Update itemPriceTax data when doing import or Incremental
	// Transfer.
	private void checkItemPriceTaxDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");
		String number = strarray[0].replace("'", "");
		String priceList = strarray[1].replace("'", "");
		String authority = strarray[2].replace("'", "");
		int salesTaxClass = Integer.parseInt(strarray[3].replace("'", ""));
		String companyCode = strarray[4].replace("'", "");

		HhItemPriceTax01 itmPrzTax = dbhelper.getItemPrzTax(number, priceList,
				companyCode);
		if (itmPrzTax != null) {
			HhItemPriceTax01 itmPrzTaxRecord = new HhItemPriceTax01();

			itmPrzTaxRecord.setHhItemPriceTax_number(number);
			itmPrzTaxRecord.setHhItemPriceTax_pricelist(priceList);
			itmPrzTaxRecord.setHhItemPriceTax_authority(authority);
			itmPrzTaxRecord.setHhItemPriceTax_salestaxclass(salesTaxClass);
			itmPrzTaxRecord.setHhItemPriceTax_companycode(companyCode);

			dbhelper.updateItemPriceTaxRecord(itmPrzTaxRecord, number,
					priceList, companyCode);
		} else {
			dbhelper.addItmPrcTaxRecord(record);
		}
	}

	// Insert or Update itemTax data when doing import or Incremental Transfer.
	private void checkItemTaxDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");
		String itemNumber = strarray[0].replace("'", "");
		String authority = strarray[1].replace("'", "");
		int salesTaxClass = Integer.parseInt(strarray[2].replace("'", ""));
		String companyCode = strarray[3].replace("'", "");

		HhItemTax01 itmTax = dbhelper.getItemTax(itemNumber, authority,
				companyCode);
		if (itmTax != null) {
			HhItemTax01 itmTaxRecord = new HhItemTax01();

			itmTaxRecord.setHhItemTax_number(itemNumber);
			itmTaxRecord.setHhItemTax_authority(authority);
			itmTaxRecord.setHhItemTax_salestaxclass(salesTaxClass);
			itmTaxRecord.setHhItemTax_companycode(companyCode);

			dbhelper.updateItemTaxRecord(itmTaxRecord, itemNumber, authority,
					companyCode);
		} else {
			dbhelper.addItemTaxRecord(record);
		}
	}

	// Insert or Update Location data when doing import or Incremental Transfer.
	private void checkLocationDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");
		String location = strarray[0].replace("'", "");
		String companycode = strarray[1].replace("'", "");

		HhLocMast01 loc = dbhelper.getLocationData(location, companycode);
		if (loc != null) {
			HhLocMast01 locRecord = new HhLocMast01();

			locRecord.setHhLocMast_location(location);
			locRecord.setHhLocMast_companycode(companycode);

			dbhelper.updateLocationData(locRecord, location, companycode);
		} else {
			dbhelper.addLocMasterRecord(record);
		}
	}

	// Insert or Update Manager data when doing import or Incremental Transfer.
	private void checkManagerDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String companyid = strarray[0].replace("'", "");
		String userid = strarray[1].replace("'", "");
		String userpass = strarray[2].replace("'", "");
		String activekey = strarray[3].replace("'", "");
		String username = strarray[4].replace("'", "");
		String locid = strarray[5].replace("'", "");
		String email = strarray[6].replace("'", "");
		String currency = strarray[7].replace("'", "");
		String pricelistcode = strarray[8].replace("'", "");
		String companynumber = strarray[9].replace("'", "");
		String companyname = strarray[10].replace("'", "");
		String companymailid = strarray[11].replace("'", "");
		int unitpriceEditable = Integer.parseInt(strarray[12].replace("'", ""));
		String accpacversion = strarray[13].replace("'", "");
		int addcustomerstate = Integer.parseInt(strarray[14].replace("'", ""));
		int customerbasedpl = Integer.parseInt(strarray[15].replace("'", ""));
		String adminpass = strarray[16].replace("'", "");

		HhManager manager = dbhelper.getManagerData(userid, companynumber);
		if (manager != null) {
			HhManager managerRecord = new HhManager();

			managerRecord.setHhManager_companyid(companyid);
			managerRecord.setHhManager_userid(userid);
			managerRecord.setHhManager_userpass(userpass);
			managerRecord.setHhManager_activekey(activekey);
			managerRecord.setHhManager_username(username);
			managerRecord.setHhManager_locid(locid);
			managerRecord.setHhManager_email(email);
			managerRecord.setHhManager_currency(currency);
			managerRecord.setHhManager_pricelistcode(pricelistcode);
			managerRecord.setHhManager_companynumber(companynumber);
			managerRecord.setHhManager_companyname(companyname);
			managerRecord.setHhManager_companymailid(companymailid);
			managerRecord.setHhManager_unitpriceeditable(unitpriceEditable);
			managerRecord.setHhManager_accpacversion(accpacversion);
			managerRecord.setHhManager_addcustomerstate(addcustomerstate);
			managerRecord.setHhManager_customerbasedpl(customerbasedpl);
			managerRecord.setHhManager_adminpass(adminpass);

			dbhelper.updateManagerData(managerRecord, userid, companynumber);

		} else {
			dbhelper.addManagerRecord(record);
		}

	}

	// Insert or Update MultiUOM data when doing import or Incremental Transfer.
	private void checkMultiUOMDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String number = strarray[0].replace("'", "");
		String qtyuom = strarray[1].replace("'", "");
		int disctype = Integer.parseInt(strarray[2].replace("'", ""));
		double price1 = Double.parseDouble(strarray[3].replace("'", ""));
		double price2 = Double.parseDouble(strarray[4].replace("'", ""));
		double price3 = Double.parseDouble(strarray[5].replace("'", ""));
		double price4 = Double.parseDouble(strarray[6].replace("'", ""));
		double price5 = Double.parseDouble(strarray[7].replace("'", ""));
		double price6 = Double.parseDouble(strarray[8].replace("'", ""));
		double qtylevel1 = Double.parseDouble(strarray[9].replace("'", ""));
		double qtylevel2 = Double.parseDouble(strarray[10].replace("'", ""));
		double qtylevel3 = Double.parseDouble(strarray[11].replace("'", ""));
		double qtylevel4 = Double.parseDouble(strarray[12].replace("'", ""));
		double qtylevel5 = Double.parseDouble(strarray[13].replace("'", ""));
		double markupcost = Double.parseDouble(strarray[14].replace("'", ""));
		String pricelistcode = strarray[15].replace("'", "");
		String location = strarray[16].replace("'", "");
		String pricinguom = strarray[17].replace("'", "");
		double pricingconvfactor = Double.parseDouble(strarray[18].replace("'",
				""));
		String markupuom = strarray[19].replace("'", "");
		double markupconvfactor = Double.parseDouble(strarray[20].replace("'",
				""));
		String companycode = strarray[21].replace("'", "");

		HhMultiUOM01 multiUOM = dbhelper.getmultiUOMData(number, location,
				companycode);
		if (multiUOM != null) {
			HhMultiUOM01 multiUomRecord = new HhMultiUOM01();

			multiUomRecord.setHhMultiUOM_number(number);
			multiUomRecord.setHhMultiUOM_qtyuom(qtyuom);
			multiUomRecord.setHhMultiUOM_disctype(disctype);
			multiUomRecord.setHhMultiUOM_price1(price1);
			multiUomRecord.setHhMultiUOM_price2(price2);
			multiUomRecord.setHhMultiUOM_price3(price3);
			multiUomRecord.setHhMultiUOM_price4(price4);
			multiUomRecord.setHhMultiUOM_price5(price5);
			multiUomRecord.setHhMultiUOM_price6(price6);
			multiUomRecord.setHhMultiUOM_qtylevel1(qtylevel1);
			multiUomRecord.setHhMultiUOM_qtylevel2(qtylevel2);
			multiUomRecord.setHhMultiUOM_qtylevel3(qtylevel3);
			multiUomRecord.setHhMultiUOM_qtylevel4(qtylevel4);
			multiUomRecord.setHhMultiUOM_qtylevel5(qtylevel5);
			multiUomRecord.setHhMultiUOM_markupcost(markupcost);
			multiUomRecord.setHhMultiUOM_pricelistcode(pricelistcode);
			multiUomRecord.setHhMultiUOM_location(location);
			multiUomRecord.setHhMultiUOM_pricinguom(pricinguom);
			multiUomRecord.setHhMultiUOM_pricingconvfactor(pricingconvfactor);
			multiUomRecord.setHhMultiUOM_markupuom(markupuom);
			multiUomRecord.setHhMultiUOM_markupconvfactor(markupconvfactor);
			multiUomRecord.setHhMultiUOM_companycode(companycode);

			dbhelper.updateMultiUomData(multiUomRecord, number, location,
					companycode);

		} else {
			dbhelper.addMultiUOMRecord(record);
		}

	}

	// Insert or Update Payment data when doing import or Incremental Transfer.
	private void checkPaymentDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String apply1 = strarray[0].replace("'", "");
		String cusnumber = strarray[1].replace("'", "");
		String cusname = strarray[2].replace("'", "");
		String documentnumber = strarray[3].replace("'", "");
		float pendingbalance = Float.parseFloat(strarray[4].replace("'", ""));
		String appliedamount = strarray[5].replace("'", "");
		float netamount = Float.parseFloat(strarray[6].replace("'", ""));
		int status = Integer.parseInt(strarray[7].replace("'", ""));
		String companycode = strarray[8].replace("'", "");

		HhPayment01 payment = dbhelper.getPaymentData(cusnumber,
				documentnumber, companycode);
		if (payment != null) {
			HhPayment01 paymentRecord = new HhPayment01();

			paymentRecord.setHhPayment_apply1(apply1);
			paymentRecord.setHhPayment_cusnumber(cusnumber);
			paymentRecord.setHhPayment_cusname(cusname);
			paymentRecord.setHhPayment_documentnumber(documentnumber);
			paymentRecord.setHhPayment_pendingbalance(pendingbalance);
			paymentRecord.setHhPayment_appliedamount(appliedamount);
			paymentRecord.setHhPayment_netamount(netamount);
			paymentRecord.setHhPayment_status(status);
			paymentRecord.setHhPayment_companycode(companycode);

			dbhelper.updatePaymentData(paymentRecord, cusnumber,
					documentnumber, companycode);
		} else {
			dbhelper.addPaymentDataRecord(record);
		}

	}

	// Insert or Update Payment data when doing import or Incremental Transfer.
	private void checkPriceDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String number = strarray[0].replace("'", "");
		int disctype = Integer.parseInt(strarray[1].replace("'", ""));
		double price1 = Double.parseDouble(strarray[2].replace("'", ""));
		double price2 = Double.parseDouble(strarray[3].replace("'", ""));
		double price3 = Double.parseDouble(strarray[4].replace("'", ""));
		double price4 = Double.parseDouble(strarray[5].replace("'", ""));
		double price5 = Double.parseDouble(strarray[6].replace("'", ""));
		double price6 = Double.parseDouble(strarray[7].replace("'", ""));
		double qtylevel1 = Double.parseDouble(strarray[8].replace("'", ""));
		double qtylevel2 = Double.parseDouble(strarray[9].replace("'", ""));
		double qtylevel3 = Double.parseDouble(strarray[10].replace("'", ""));
		double qtylevel4 = Double.parseDouble(strarray[11].replace("'", ""));
		double qtylevel5 = Double.parseDouble(strarray[12].replace("'", ""));
		double markupcost = Double.parseDouble(strarray[13].replace("'", ""));
		String pricelistcode = strarray[14].replace("'", "");
		String location = strarray[15].replace("'", "");
		String pricinguom = strarray[16].replace("'", "");
		double pricingconvfactor = Double.parseDouble(strarray[17].replace("'",
				""));
		String markupuom = strarray[18].replace("'", "");
		double markupconvfactor = Double.parseDouble(strarray[19].replace("'",
				""));
		double pricetype = Double.parseDouble(strarray[20].replace("'", ""));
		String companycode = strarray[21].replace("'", "");

		HhPrice01 price = dbhelper.getPrcData(pricelistcode, number,
				companycode);
		if (price != null) {
			HhPrice01 priceRecord = new HhPrice01();

			priceRecord.setHhPrice_number(number);
			priceRecord.setHhPrice_disctype(disctype);
			priceRecord.setHhPrice_price1(price1);
			priceRecord.setHhPrice_price2(price2);
			priceRecord.setHhPrice_price3(price3);
			priceRecord.setHhPrice_price4(price4);
			priceRecord.setHhPrice_price5(price5);
			priceRecord.setHhPrice_price6(price6);
			priceRecord.setHhPrice_qtylevel1(qtylevel1);
			priceRecord.setHhPrice_qtylevel2(qtylevel2);
			priceRecord.setHhPrice_qtylevel3(qtylevel3);
			priceRecord.setHhPrice_qtylevel4(qtylevel4);
			priceRecord.setHhPrice_qtylevel5(qtylevel5);
			priceRecord.setHhPrice_markup_cost(markupcost);
			priceRecord.setHhPrice_price_list_code(pricelistcode);
			priceRecord.setHhPrice_location(location);
			priceRecord.setHhPrice_pricing_uom(pricinguom);
			priceRecord.setHhPrice_pricing_conv_factor(pricingconvfactor);
			priceRecord.setHhPrice_markup_uom(markupuom);
			priceRecord.setHhPrice_markup_conv_factor(markupconvfactor);
			priceRecord.setHhPrice_price_type(pricetype);
			priceRecord.setHhPrice_companycode(companycode);

			dbhelper.updatePriceData(priceRecord, pricelistcode, number,
					companycode);

		} else {
			dbhelper.addPriceRecord(record);
		}
	}

	// Insert or Update PricListMaster data when doing import or Incremental
	// Transfer.
	private void checkPriceListMasterDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String pricelist = strarray[0].replace("'", "");
		String companycode = strarray[1].replace("'", "");

		HhPriceListMaster01 priceList = dbhelper.getPriceListData(pricelist,
				companycode);
		if (priceList != null) {
			HhPriceListMaster01 priceListRecord = new HhPriceListMaster01();

			priceListRecord.setPriceList(pricelist);
			priceListRecord.setPriceList_companycode(companycode);

			dbhelper.updatePriceListData(priceListRecord, pricelist,
					companycode);
		} else {
			dbhelper.addPriceListMasterRecord(record);
		}
	}

	// Insert or Update PricListMaster data when doing import or Incremental
	// Transfer.
	private void checkReceiptTypeDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String receipttype = strarray[0].replace("'", "");
		String receiptname = strarray[1].replace("'", "");
		String companycode = strarray[2].replace("'", "");

		HhReceiptType01 receiptType = dbhelper.getReceiptTypeData(receipttype,
				companycode);

		if (receiptType != null) {
			HhReceiptType01 receiptTypeRecord = new HhReceiptType01();

			receiptTypeRecord.setHhReceiptType_receipttype(receipttype);
			receiptTypeRecord.setHhReceiptType_receiptname(receiptname);
			receiptTypeRecord.setHhReceiptType_companycode(companycode);

			dbhelper.updateReceiptTypeData(receiptTypeRecord, receipttype,
					companycode);
		} else {
			dbhelper.addReceiptTypeRecord(record);
		}
	}

	// Insert or Update Route data when doing import or Incremental Transfer.
	private void checkRouteDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String customernumber = strarray[0].replace("'", "");
		String customername = strarray[1].replace("'", "");
		int dayofvisit = Integer.parseInt(strarray[2].replace("'", ""));
		int monthofvisit = Integer.parseInt(strarray[3].replace("'", ""));
		int yearofvisit = Integer.parseInt(strarray[4].replace("'", ""));
		int visitstat = Integer.parseInt(strarray[5].replace("'", ""));
		String companycode = strarray[6].replace("'", "");

		HhRoute01 route = dbhelper.getRouteData(customernumber, companycode);
		if (route != null) {
			HhRoute01 routeRecord = new HhRoute01();

			routeRecord.setHhRoute_customernumber(customernumber);
			routeRecord.setHhRoute_customername(customername);
			routeRecord.setHhRoute_dayofvisit(dayofvisit);
			routeRecord.setHhRoute_monthofvisit(monthofvisit);
			routeRecord.setHhRoute_yearofvisit(yearofvisit);
			routeRecord.setHhRoute_visitstat(visitstat);
			routeRecord.setHhRoute_companycode(companycode);

			dbhelper.updateRouteData(routeRecord, customernumber, companycode);
		} else {
			dbhelper.addRouteRecord(record);
		}
	}

	// Insert or Update ShipTo data when doing import or Incremental Transfer.
	private void checkShipToDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String customernumber = strarray[0].replace("'", "");
		int primaryshipto = Integer.parseInt(strarray[1].replace("'", ""));
		String loccode = strarray[2].replace("'", "");
		String description = strarray[3].replace("'", "");
		String shipvia = strarray[4].replace("'", "");
		String viadescription = strarray[5].replace("'", "");
		String address = strarray[6].replace("'", "");
		String city = strarray[7].replace("'", "");
		String state = strarray[8].replace("'", "");
		String zip = strarray[9].replace("'", "");
		String country = strarray[10].replace("'", "");
		String contact = strarray[11].replace("'", "");
		String taxgroup = strarray[12].replace("'", "");
		String taxauthority1 = strarray[13].replace("'", "");
		int taxable1 = Integer.parseInt(strarray[14].replace("'", ""));
		String taxstts1 = strarray[15].replace("'", "");
		String taxauthority2 = strarray[16].replace("'", "");
		int taxable2 = Integer.parseInt(strarray[17].replace("'", ""));
		String taxstts2 = strarray[18].replace("'", "");
		String taxauthority3 = strarray[19].replace("'", "");
		int taxable3 = Integer.parseInt(strarray[20].replace("'", ""));
		String taxstts3 = strarray[21].replace("'", "");
		String taxauthority4 = strarray[22].replace("'", "");
		int taxable4 = Integer.parseInt(strarray[23].replace("'", ""));
		String taxstts4 = strarray[24].replace("'", "");
		String taxauthority5 = strarray[25].replace("'", "");
		int taxable5 = Integer.parseInt(strarray[26].replace("'", ""));
		String taxstts5 = strarray[27].replace("'", "");
		String companycode = strarray[28].replace("'", "");

		HhShipTo01 shipToData = dbhelper.getShipToData(customernumber,
				companycode);
		if (shipToData != null) {
			HhShipTo01 shipToDataRecord = new HhShipTo01();

			shipToDataRecord.setHhShipTo_customerNumber(customernumber);
			shipToDataRecord.setHhShipTo_primaryShipTo(primaryshipto);
			shipToDataRecord.setHhShipTo_locCode(loccode);
			shipToDataRecord.setHhShipTo_description(description);
			shipToDataRecord.setHhShipTo_ship_via(shipvia);
			shipToDataRecord.setHhShipTo_via_description(viadescription);
			shipToDataRecord.setHhShipTo_address(address);
			shipToDataRecord.setHhShipTo_city(city);
			shipToDataRecord.setHhShipTo_state(state);
			shipToDataRecord.setHhShipTo_zip(zip);
			shipToDataRecord.setHhShipTo_country(country);
			shipToDataRecord.setHhShipTo_contact(contact);
			shipToDataRecord.setHhShipTo_tax_group(taxgroup);
			shipToDataRecord.setHhShipTo_taxauthority1(taxauthority1);
			shipToDataRecord.setHhShipTo_taxable1(taxable1);
			shipToDataRecord.setHhShipTo_taxstts1(taxstts1);
			shipToDataRecord.setHhShipTo_taxauthority2(taxauthority2);
			shipToDataRecord.setHhShipTo_taxable2(taxable2);
			shipToDataRecord.setHhShipTo_taxstts2(taxstts2);
			shipToDataRecord.setHhShipTo_taxauthority3(taxauthority3);
			shipToDataRecord.setHhShipTo_taxable3(taxable3);
			shipToDataRecord.setHhShipTo_taxstts3(taxstts3);
			shipToDataRecord.setHhShipTo_taxauthority4(taxauthority4);
			shipToDataRecord.setHhShipTo_taxable4(taxable4);
			shipToDataRecord.setHhShipTo_taxstts4(taxstts4);
			shipToDataRecord.setHhShipTo_taxauthority5(taxauthority5);
			shipToDataRecord.setHhShipTo_taxable5(taxable5);
			shipToDataRecord.setHhShipTo_taxstts5(taxstts5);
			shipToDataRecord.setHhShipTo_companycode(companycode);

			dbhelper.updateShipToData(shipToDataRecord, customernumber,
					companycode);
		} else {
			dbhelper.addShipToRecord(record);
		}
	}

	// Insert or Update ShipVia data when doing import or Incremental Transfer.
	private void checkShipViaDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String code = strarray[0].replace("'", "");
		String name = strarray[1].replace("'", "");
		String companycode = strarray[2].replace("'", "");

		HhShipVia01 shipVia = dbhelper.getShipViaData(code, companycode);

		if (shipVia != null) {
			HhShipVia01 shipViaRecord = new HhShipVia01();

			shipViaRecord.setHhShipVia_code(code);
			shipViaRecord.setHhShipVia_name(name);
			shipViaRecord.setHhShipVia_companycode(companycode);

			dbhelper.updateShipViaData(shipViaRecord, code, companycode);
		} else {
			dbhelper.addShipViaRecord(record);
		}
	}

	// Insert or Update TaxClass data when doing import or Incremental Transfer.
	private void checkTaxClassDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String taxauthority = strarray[0].replace("'", "");
		String taxstts = strarray[1].replace("'", "");
		String description = strarray[2].replace("'", "");
		String companycode = strarray[3].replace("'", "");

		HhTaxClass01 taxClass = dbhelper.getTaxClassData(taxauthority, taxstts,
				companycode);

		if (taxClass != null) {
			HhTaxClass01 taxClassRecord = new HhTaxClass01();

			taxClassRecord.setHhTaxClass_taxauthority(taxauthority);
			taxClassRecord.setHhTaxClass_taxstts(taxstts);
			taxClassRecord.setHhTaxClass_description(description);
			taxClassRecord.setHhTaxClass_companycode(companycode);

			dbhelper.updateTaxClassData(taxClassRecord, taxauthority, taxstts,
					companycode);
		} else {
			dbhelper.addTaxClassRecord(record);
		}
	}

	// Insert or Update TaxGroup data when doing import or Incremental Transfer.
	private void checkTaxGroupDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String taxgroup = strarray[0].replace("'", "");
		String taxauthority1 = strarray[1].replace("'", "");
		String taxstts1 = strarray[2].replace("'", "");
		int taxable1 = Integer.parseInt(strarray[3].replace("'", ""));
		String taxauthority2 = strarray[4].replace("'", "");
		String taxstts2 = strarray[5].replace("'", "");
		int taxable2 = Integer.parseInt(strarray[6].replace("'", ""));
		String taxauthority3 = strarray[7].replace("'", "");
		String taxstts3 = strarray[8].replace("'", "");
		int taxable3 = Integer.parseInt(strarray[9].replace("'", ""));
		String taxauthority4 = strarray[10].replace("'", "");
		String taxstts4 = strarray[11].replace("'", "");
		int taxable4 = Integer.parseInt(strarray[12].replace("'", ""));
		String taxauthority5 = strarray[13].replace("'", "");
		String taxstts5 = strarray[14].replace("'", "");
		int taxable5 = Integer.parseInt(strarray[15].replace("'", ""));
		String companycode = strarray[16].replace("'", "");

		HhTaxGroup01 taxGroup = dbhelper.getTaxGroupAuthority(taxgroup,
				companycode);
		if (taxGroup != null) {
			HhTaxGroup01 taxGroupRecord = new HhTaxGroup01();

			taxGroupRecord.setHhTaxGroup_taxgroup(taxgroup);
			taxGroupRecord.setHhTaxGroup_taxauthority1(taxauthority1);
			taxGroupRecord.setHhTaxGroup_taxstts1(taxstts1);
			taxGroupRecord.setHhTaxGroup_taxable1(taxable1);
			taxGroupRecord.setHhTaxGroup_taxauthority2(taxauthority2);
			taxGroupRecord.setHhTaxGroup_taxstts2(taxstts2);
			taxGroupRecord.setHhTaxGroup_taxable2(taxable2);
			taxGroupRecord.setHhTaxGroup_taxauthority3(taxauthority3);
			taxGroupRecord.setHhTaxGroup_taxstts3(taxstts3);
			taxGroupRecord.setHhTaxGroup_taxable3(taxable3);
			taxGroupRecord.setHhTaxGroup_taxauthority4(taxauthority4);
			taxGroupRecord.setHhTaxGroup_taxstts4(taxstts4);
			taxGroupRecord.setHhTaxGroup_taxable4(taxable4);
			taxGroupRecord.setHhTaxGroup_taxauthority5(taxauthority5);
			taxGroupRecord.setHhTaxGroup_taxstts5(taxstts5);
			taxGroupRecord.setHhTaxGroup_taxable5(taxable5);
			taxGroupRecord.setHhTaxGroup_companycode(companycode);

			dbhelper.updateTaxGroupData(taxGroupRecord, taxgroup, companycode);
		} else {
			dbhelper.addTaxGroupRecord(record);
		}
	}

	// Insert or Update TaxRate data when doing import or Incremental Transfer.
	private void checkTaxRateDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String authority = strarray[0].replace("'", "");
		int buyerclass = Integer.parseInt(strarray[1].replace("'", ""));
		double itemrate1 = Double.parseDouble(strarray[2].replace("'", ""));
		double itemrate2 = Double.parseDouble(strarray[3].replace("'", ""));
		double itemrate3 = Double.parseDouble(strarray[4].replace("'", ""));
		double itemrate4 = Double.parseDouble(strarray[5].replace("'", ""));
		double itemrate5 = Double.parseDouble(strarray[6].replace("'", ""));
		double itemrate6 = Double.parseDouble(strarray[7].replace("'", ""));
		double itemrate7 = Double.parseDouble(strarray[8].replace("'", ""));
		double itemrate8 = Double.parseDouble(strarray[9].replace("'", ""));
		double itemrate9 = Double.parseDouble(strarray[10].replace("'", ""));
		double itemrate10 = Double.parseDouble(strarray[11].replace("'", ""));
		String companycode = strarray[12].replace("'", "");

		HhTaxRate01 taxRate = dbhelper.getTaxRateData(authority, buyerclass,
				companycode);
		if (taxRate != null) {
			HhTaxRate01 taxRateRecord = new HhTaxRate01();

			taxRateRecord.setHhTaxRate_authority(authority);
			taxRateRecord.setHhTaxRate_buyerclass(buyerclass);
			taxRateRecord.setHhTaxRate_itemrate1(itemrate1);
			taxRateRecord.setHhTaxRate_itemrate2(itemrate2);
			taxRateRecord.setHhTaxRate_itemrate3(itemrate3);
			taxRateRecord.setHhTaxRate_itemrate4(itemrate4);
			taxRateRecord.setHhTaxRate_itemrate5(itemrate5);
			taxRateRecord.setHhTaxRate_itemrate6(itemrate6);
			taxRateRecord.setHhTaxRate_itemrate7(itemrate7);
			taxRateRecord.setHhTaxRate_itemrate8(itemrate8);
			taxRateRecord.setHhTaxRate_itemrate9(itemrate9);
			taxRateRecord.setHhTaxRate_itemrate10(itemrate10);
			taxRateRecord.setHhTaxRate_companycode(companycode);

			dbhelper.updateTaxRateData(taxRateRecord, authority, buyerclass,
					companycode);
		} else {
			dbhelper.addTaxRateRecord(record);
		}
	}

	// Insert or Update Terms data when doing import or Incremental Transfer.
	private void checkTermsDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String codeterm = strarray[0].replace("'", "");
		int countpayment = Integer.parseInt(strarray[1].replace("'", ""));
		double percentdue = Double.parseDouble(strarray[2].replace("'", ""));
		double percentdisc = Double.parseDouble(strarray[3].replace("'", ""));
		int numberofdays = Integer.parseInt(strarray[4].replace("'", ""));
		int duedays = Integer.parseInt(strarray[5].replace("'", ""));
		int discwithtax = Integer.parseInt(strarray[6].replace("'", ""));
		String companycode = strarray[7].replace("'", "");

		HhTerms01 terms = dbhelper.getTermsData(codeterm, countpayment,
				companycode);
		if (terms != null) {
			HhTerms01 termsRecord = new HhTerms01();

			termsRecord.setHhTerms_codeterm(codeterm);
			termsRecord.setHhTerms_countpayment(countpayment);
			termsRecord.setHhTerms_percentdue(percentdue);
			termsRecord.setHhTerms_percentdisc(percentdisc);
			termsRecord.setHhTerms_numberdays(numberofdays);
			termsRecord.setHhTerms_duedays(duedays);
			termsRecord.setHhTerms_discwithtax(discwithtax);
			termsRecord.setHhTerms_companycode(companycode);

			dbhelper.updateTermsData(termsRecord, codeterm, countpayment,
					companycode);
		} else {
			dbhelper.addTermsRecord(record);
		}
	}

	// Insert or Update UPC data when doing import or Incremental Transfer.
	private void checkUPCDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String itemnumber = strarray[0].replace("'", "");
		String upcnumber = strarray[1].replace("'", "");
		String itemdesc = strarray[2].replace("'", "");
		String companycode = strarray[3].replace("'", "");

		HhUPC01 upc = dbhelper.getUPCData(itemnumber, upcnumber, companycode);
		if (upc != null) {
			HhUPC01 upcRecord = new HhUPC01();

			upcRecord.setHhUPC_itemnumber(itemnumber);
			upcRecord.setHhUPC_upcnumber(upcnumber);
			upcRecord.setHhUPC_itemdesc(itemdesc);
			upcRecord.setHhUPC_companycode(companycode);

			dbhelper.updateUPCData(upcRecord, itemnumber, upcnumber,
					companycode);
		} else {
			dbhelper.addUPCRecord(record);
		}
	}

	// Insert or Update Manf_Number data when doing import or Incremental
	// Transfer.
	private void checkManfNumberDataForInsertorUpdate(String record) {
		String[] strarray = record.split(",");

		String manitemno = strarray[0].replace("'", "");
		String itemno = strarray[1].replace("'", "");
		String uom = strarray[2].replace("'", "");
		String companycode = strarray[3].replace("'", "");

		Manf_Number01 manf = dbhelper.getManufData(manitemno, itemno, uom,
				companycode);
		if (manf != null) {
			Manf_Number01 manfRecord = new Manf_Number01();

			manfRecord.setManuf_manitemno(manitemno);
			manfRecord.setManuf_itemno(itemno);
			manfRecord.setManuf_uom(uom);
			manfRecord.setCompany_code(companycode);

			dbhelper.updateManfData(manfRecord, manitemno, itemno, uom,
					companycode);
		} else {
			dbhelper.addManufactureNumberDataRecord(record);
		}
	}

	// Insert or Update mspdb data when doing import or Incremental Transfer.
	private void checkMsbDbDataForInsertorUpdate(String record) {

		String[] strarray = record.split(",");

		String companyNo = strarray[0].replace("'", "");
		String salesPerson = strarray[1].replace("'", "");
		int mapNo = Integer.parseInt(strarray[2].replace("'", ""));
		int custNo = Integer.parseInt(strarray[3].replace("'", ""));
		int ordNo = Integer.parseInt(strarray[4].replace("'", ""));
		int quoteNo = Integer.parseInt(strarray[5].replace("'", ""));
		int lastInvNo = Integer.parseInt(strarray[6].replace("'", ""));
		// int receiptNo = Integer.parseInt(strarray[7].replace("'", ""));

		Mspdb mspdb = dbhelper.getMspDbData(companyNo,
				supporter.getTempSalesPersonCode());

		if (mspdb != null) {

			if (mapNo > mspdb.getMspdb_mapNo()
					|| custNo > mspdb.getMspdb_customerNumber()
					|| ordNo > mspdb.getMspdb_orderNumber()
					|| quoteNo > mspdb.getMspdb_quoteNumber()
					|| lastInvNo > mspdb.getMspdb_lastinvno()
					|| lastInvNo > ordNo) {

				Mspdb mspdbRecord = new Mspdb();

				mspdbRecord.setMspdb_companyNumber(mspdb
						.getMspdb_companyNumber());
				mspdbRecord.setMspdb_salesPerson(mspdb.getMspdb_salesPerson());

				if (mapNo > mspdb.getMspdb_mapNo()) {
					mspdbRecord.setMspdb_mapNo(mapNo);
				}

				if (custNo > mspdb.getMspdb_customerNumber()) {
					mspdbRecord.setMspdb_customerNumber(custNo);
				}

				if (ordNo > mspdb.getMspdb_orderNumber()) {
					mspdbRecord.setMspdb_orderNumber(ordNo);
				}

				if (quoteNo > mspdb.getMspdb_quoteNumber()) {
					mspdbRecord.setMspdb_quoteNumber(quoteNo);
				}

				if (lastInvNo > mspdb.getMspdb_lastinvno()) {
					mspdbRecord.setMspdb_lastinvno(lastInvNo); // jun 28 added
				}

				if (lastInvNo > ordNo) { // Jul 2nd added for checking invoice
											// no is greterthan order no
					mspdbRecord.setMspdb_orderNumber(lastInvNo);
				}

				dbhelper.updateMspDb(mspdbRecord, supporter.getCompanyNo());
			}
		} else {
			Mspdb mspdbRecord = new Mspdb();
			mspdbRecord.setMspdb_companyNumber(companyNo);
			mspdbRecord.setMspdb_salesPerson(salesPerson);
			mspdbRecord.setMspdb_mapNo(mapNo);
			mspdbRecord.setMspdb_customerNumber(custNo);
			if (lastInvNo > ordNo) {
				mspdbRecord.setMspdb_orderNumber(lastInvNo);
			} else {
				mspdbRecord.setMspdb_orderNumber(ordNo);
			}
			mspdbRecord.setMspdb_quoteNumber(quoteNo);
			mspdbRecord.setMspdb_lastinvno(lastInvNo);
			// mspdbRecord.setMspdb_receiptnumber(receiptNo);

			dbhelper.addMspDbData(mspdbRecord);
		}

	}

	private void licenseDataLoad(String record) {

		String installDate = "";
		String noOfDays = "";
		SimpleDateFormat dateFormater = new SimpleDateFormat("dd-MM-yyyy");
		SimpleDateFormat incomingDateFormater = new SimpleDateFormat(
				"MM/dd/yyyy hh:mm:ss a"); // old format(5/6/2013)
		// SimpleDateFormat incomingDateFormater= new
		// SimpleDateFormat("MM/dd/yyyy");

		if (record.equals("")) {

			Date today = new Date();

			HhLicense license = new HhLicense();
			license.setInstallDate(dateFormater.format(today));
			license.setNoOfDays("30");

			writeLicenceData(license);
			LogfileCreator
					.appendLog("License data not available..Trail version installed");
		} else {
			String[] strarray = record.split(",");

			installDate = strarray[0].replace("'", "");
			noOfDays = strarray[1].replace("'", "");

			if (installDate.equals("") || noOfDays.equals("")) {
				Date today = new Date();

				HhLicense license = new HhLicense();
				license.setInstallDate(dateFormater.format(today));
				license.setNoOfDays("30");
				writeLicenceData(license);
				LogfileCreator
						.appendLog("License data not available..Trail version installed");
			} else {

				try {
					Date incomingDate = incomingDateFormater.parse(installDate);

					String fdate = dateFormater.format(incomingDate);

					HhLicense license = new HhLicense();
					license.setInstallDate(fdate);

					String devId = supporter.getDeviceId();

					if (devId.startsWith("0000")) {
						license.setNoOfDays("30");
						LogfileCreator
								.appendLog("Device id not available..Trail version installed");
					} else {
						license.setNoOfDays(noOfDays);
					}

					writeLicenceData(license);

				} catch (ParseException e) {
					e.printStackTrace();
					LogfileCreator.appendLog(e.getMessage());
				}

			}
		}

	}

}