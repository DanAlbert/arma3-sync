/* HeaderMaker.java

   HeaderMaker: Simple header-maker for metafiles
   Copyright (C) 2011 Tomáš Hlavni�?ka <hlavntom@fel.cvut.cz>

   This file is a part of Jazsync.

   Jazsync is free software; you can redistribute it and/or modify it
   under the terms of the GNU General Public License as published by the
   Free Software Foundation; either version 2 of the License, or (at
   your option) any later version.

   Jazsync is distributed in the hope that it will be useful, but
   WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   General Public License for more details.

   You should have received a copy of the GNU General Public License
   along with Jazsync; if not, write to the

      Free Software Foundation, Inc.,
      59 Temple Place, Suite 330,
      Boston, MA  02111-1307
      USA
 */

package fr.soe.a3s.jazsyncmake;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

import fr.soe.a3s.jazsync.SHA1;
/**
 * Simple class for creating headers for metafile
 * @author Tomáš Hlavni�?ka
 */
public class HeaderMaker {
/*
zsync: 0.6.1
Filename: tinycore.iso
MTime: Sat, 06 Mar 2010 09:33:36 +0000
Blocksize: 2048
Length: 11483136
Hash-Lengths: 2,2,5
URL: http://i.iinfo.cz/files/root/240/tinycore.iso
SHA-1: 5944ec77b9b0f2d6b8212d142970117f5801430a
*/


    private String Version="zsync: ";
    private String Filename="Filename: ";
    private String MTime="MTime: ";
    private String Blocksize="Blocksize: ";
    private String Length="Length: ";
    private String HashLengths="Hash-Lengths: ";
    private String URL="URL: ";
    private String ZURL="Z-URL: "; 
    private String SHA1="SHA-1: ";
    private String ZMAP2="Z-Map2: ";
    private SHA1 sha1;

    private int seq_num=1;
    private int rsum_bytes=4;
    private int checksum_bytes=16;
    private long mtime=0;

    public HeaderMaker(File sourceFile, String filename, String url,String sha1, int blocksize, int[] hashLengths){
        Version+="jazsync";
        this.mtime=sourceFile.lastModified();        
        this.Filename += sourceFile.getName();
        MTime+=setMTime("EEE, dd MMM yyyy HH:mm:ss Z");
        Length+=sourceFile.length();
        this.URL += url;

        if (isPowerOfTwo(blocksize)) {
            Blocksize+=blocksize;
        } else {
            System.out.println("Blocksize must be a power of 2 (512, 1024, 2048, ...)");
        }

        this.seq_num=hashLengths[0];
        this.rsum_bytes=hashLengths[1];
        this.checksum_bytes=hashLengths[2];
        HashLengths+=(this.seq_num+","+this.rsum_bytes+","+this.checksum_bytes);
        this.SHA1 += sha1;
    }

    /**
     * Checks if <code>number</code> is power of two
     * @param number Number to be checked
     * @return Boolean value
     */
    private boolean isPowerOfTwo(int number){
        boolean isPowerOfTwo = true;
        while(number>1){
            if(number%2 != 0){
                isPowerOfTwo = false;
                break;
            } else {
                number=number/2;
            }
        }
        return isPowerOfTwo;
    }


    /**
     * Converts time (ms) into formated MTime using <code>dateFormat</code>
     * @param dateFormat MTime format
     * @return Formated date of MTime
     */
    private String setMTime(String dateFormat) {
        Date date = new Date();
        date.setTime(mtime);
        SimpleDateFormat sdf = new SimpleDateFormat(dateFormat,Locale.US);
        return sdf.format(date);
    }

    /**
     * Method builds header from key values
     * @return Full header in String format
     */
    public String getFullHeader(){
        StringBuilder sb = new StringBuilder("");
        sb.append(Version).append("\n");
        sb.append(Filename).append("\n");
        sb.append(MTime).append("\n");
        sb.append(Blocksize).append("\n");
        sb.append(Length).append("\n");
        sb.append(HashLengths).append("\n");
        sb.append(URL).append("\n");
        sb.append(SHA1).append("\n\n");
        String header = sb.toString();
        return header;
    }
}