/*
   SHA1: SHA1 message digest algorithm.
   Copyright (C) 2011 Tomas Hlavnicka <hlavntom@fel.cvut.cz>

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

package fr.soe.a3s.service.jazsync;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

/**
 * Class for SHA-1 sum
 * @author Tomáš Hlavni�?ka
 */
public class SHA1 {
    private File file;
    private FileInputStream fis;
    private MessageDigest sha1;
    private StringBuilder sb;

    /**
     * Constructor SHA1
     * @param file File for calculation
     */
    public SHA1(File file){
        this.file = file;
    }

    /**
     * Calculates SHA1
     * @return String with hash value
     */
    public String SHA1sum(){
        try {
            sha1 = MessageDigest.getInstance("SHA1");
            fis = new FileInputStream(file);
            byte[] dataBytes = new byte[1024];

            int read = 0;

            while ((read = fis.read(dataBytes)) != -1) {
              sha1.update(dataBytes, 0, read);
            }

            byte[] mdbytes = sha1.digest();

            //prevede byte do hex formatu
            sb = new StringBuilder("");
            for (int i = 0; i < mdbytes.length; i++) {
                sb.append(Integer.toString((mdbytes[i] & 0xff) + 0x100, 16).substring(1));
            }
            fis.close();
        } catch (IOException ex) {
            System.out.println("Can't read file to count SHA-1 hash, check your permissions");
            //System.exit(1);
        } catch (NoSuchAlgorithmException ex) {
            System.out.println("Problem with SHA-1 hash");
            //System.exit(1);
        }
        return sb.toString();
    }  
} 
