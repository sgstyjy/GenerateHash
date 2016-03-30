package generatehash;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import jxl.JXLException;
import jxl.Workbook;
import jxl.write.Label;
import jxl.write.WritableSheet;
import jxl.write.WritableWorkbook;
import jxl.write.biff.JxlWriteException;

public class GenerateHash {
	
	public static void main(String[] args) throws IOException, JxlWriteException, JXLException{
		
		//the input image file
		String infile =  args[0];
		Constant.imagefile = Constant.base_path + infile;
		System.out.println("The input image file is:" + Constant.imagefile);
		
		//construct the output file name
		StringBuilder strbuilder1 = new StringBuilder();
		String frontpart = Constant.imagefile.split("\\.")[0];
		strbuilder1.append(frontpart);
		if(Constant.blocksize==1024)
			strbuilder1.append("-1.xls");
		else
			strbuilder1.append("-4.xls");
		Constant.hashtable = strbuilder1.toString();
		System.out.println("The output hash table name is:" + Constant.hashtable);
		
		
		File file_in = new File(Constant.imagefile);
		InputStream reader = new FileInputStream(file_in);
		
		//the output hashtable
		File file_out = new File(Constant.hashtable);
		OutputStream writer = new FileOutputStream(file_out);
		WritableWorkbook workbook = Workbook.createWorkbook(writer);
		WritableSheet sheet = workbook.createSheet("Hashtable",0);

		//call hash functions
		int blocknum = 0;
		int position = 0;
		int size = reader.available();     //the total image size in byte
		byte[] bb = new byte[Constant.blocksize];
		String temp = null;
		String tempabs = null;
		long bkdrabs = 0;

		BKDR bkdrhasher = new BKDR();
		
		Long starttime = System.currentTimeMillis();
		while(position<size){
			//special tackle the last block
			/*if((size-position)<Constant.blocksize){
				byte[] lastbf = new byte[(size-position)];
				reader.read(lastbf);
				temp = new String(lastbf);
				bkdrabs = bkdrhasher.bkdrhash(temp);	    	
				tempabs = Long.toString(bkdrabs);
				Label tempcell = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, tempabs);
				sheet.addCell(tempcell);
				break;
			}*/
			reader.read(bb);
			temp = new String (bb);
			bkdrabs = bkdrhasher.bkdrhash(temp);    	
			tempabs = Long.toString(bkdrabs);
			Label tempcell = new Label (blocknum/Constant.COLUMNS,blocknum%Constant.COLUMNS, tempabs);
			sheet.addCell(tempcell);
			position += Constant.blocksize;
			blocknum++;
		}
		Long endtime = System.currentTimeMillis();
		Long duration = endtime-starttime;
		
		System.out.println("Total block: "+blocknum);
		System.out.println("Total hasttime: "+duration);
		workbook.write();
		workbook.close();
		reader.close();
		return;
	}
}
