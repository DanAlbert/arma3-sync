/* jazsync.java

   Jazsync: Main method
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

package fr.soe.a3s.jazsync;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;

import fr.soe.a3s.dao.connection.HttpDAO;
import fr.soe.a3s.dao.connection.MyHttpConnection;
import fr.soe.a3s.domain.AbstractProtocole;
import fr.soe.a3s.exception.HttpException;
import fr.soe.a3s.jazsyncmake.MetaFileMaker;

/**
 * Modified code of Jazsync v0.8.9
 */
public class Jazsync {

    /**
     * Generate .zsync file
     * 
     * @param sourcefile
     * @param url
     * @param sha1
     * @throws Exception
     */
    public static void make(File sourcefile, File zsyncFile, String url, String sha1)
            throws IOException {

        assert (sourcefile != null);
        assert (sourcefile.exists());
        assert (!sourcefile.isDirectory());
        assert (url != null);
        assert (sha1 != null);

        MetaFileMaker mfm = new MetaFileMaker(sourcefile, zsyncFile, url, sha1);
    }

    /**
     * Sync file with server
     * 
     * @param targetFile
     * @param targetFileSha1
     * @param relativeFileUrl
     * @param relativeZsyncFileUrl
     * @param hostname
     * @param login
     * @param password
     * @param port
     * @param readTimeOut
     * @param connectionTimeOut
     * @param resume
     * @param httpDAO
     * @throws HttpException
     * @throws Exception
     * @throws FileNotFoundException
     */
    public static void sync(File targetFile, String targetFileSha1, String targetRelativeFileUrl,
            String relativeZsyncFileUrl, AbstractProtocole protocole, HttpDAO httpDAO)
            throws IOException, HttpException {

        assert (targetFile != null);
        assert (relativeZsyncFileUrl != null);

        MyHttpConnection http = new MyHttpConnection(protocole, httpDAO);
        httpDAO.setConnexion(http);
        MetaFileReader mfr = new MetaFileReader(relativeZsyncFileUrl, http);
        FileMaker fm = new FileMaker(mfr, http);
        fm.sync(targetFile, targetFileSha1, targetRelativeFileUrl);
    }

    /**
     * Determine file completion in %
     * 
     * @throws HttpException
     * @throws Exception
     */
    public static double getCompletion(File targetFile, String targetFileSha1,
            String relativeZsyncFileUrl, AbstractProtocole protocole, HttpDAO httpDAO)
            throws IOException, HttpException {

        assert (targetFile != null);
        assert (relativeZsyncFileUrl != null);

        if (!targetFile.exists()) {
            return 0;
        }
        else {
            MyHttpConnection http = new MyHttpConnection(protocole, httpDAO);
            httpDAO.setConnexion(http);
            MetaFileReader mfr = new MetaFileReader(relativeZsyncFileUrl, http);
            FileMaker fm = new FileMaker(mfr, http);
            double completion = fm.getCompletion(targetFile, targetFileSha1);
            return completion;
        }
    }
}
