/*
 * Copyright (C) 2024 Longri
 *
 * This file is part of Logging.
 *
 * Logging is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * any later version.
 *
 * Logging is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Logging. If not, see <https://www.gnu.org/licenses/>.
 */
package de.longri.logging;

import org.slf4j.LoggerFactory;

import static de.longri.logging.LongriLogger.CONFIG_PARAMS;

public class LongriLoggerInit {

    static {
        //initial Logger
        try {


            LongriLoggerConfiguration.setConfigurationFile(LongriLoggerInit.class.getClassLoader().getResourceAsStream("logger/longriLogger.properties"));
            LongriLoggerFactory factory = ((LongriLoggerFactory) LoggerFactory.getILoggerFactory());
            factory.reset();
            LongriLoggerInit.init();


            //Exclude some Classes from debug Logging
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.share.FileInputStream", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB2CreditGrantingPacketHandler", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB2SignatureVerificationPacketHandler", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.packet.SMB3DecryptingPacketHandler", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.transport.tcp.direct.DirectTcpPacketReader", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.transport.tcp.direct.DirectTcpTransport", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.Connection", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.protocol.commons.concurrent.Promise", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.protocol.commons.socket.ProxySocketFactory", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.SMBProtocolNegotiator", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.PacketEncryptor", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.auth.NtlmAuthenticator", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.connection.SMBSessionBuilder", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.asn1.ASN1InputStream", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.ntlm.messages.NtlmChallenge", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.hierynomus.smbj.session.Session", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:de.longri.fx.file_handle_tree_view.FileHandleTreeItem", "error");
            CONFIG_PARAMS.setProperty("longriLogger.logLevel:com.longri.prtg.backup.crontab.Main", "debug");


        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void init() {
        LongriLogger.resetLazyInit();
        LongriLogger.lazyInit();
    }

}
