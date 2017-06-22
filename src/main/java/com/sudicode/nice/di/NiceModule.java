package com.sudicode.nice.di;

import com.google.inject.AbstractModule;
import com.google.inject.Provides;
import com.google.inject.Singleton;
import com.mysql.cj.jdbc.MysqlDataSource;
import com.sudicode.nice.Constants;
import com.sudicode.nice.hardware.CardTerminalDevice;
import com.sudicode.nice.hardware.Device;
import com.sudicode.nice.ui.DialogFactory;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.smartcardio.CardException;
import javax.smartcardio.CardTerminal;
import javax.smartcardio.TerminalFactory;
import javax.sql.DataSource;
import java.util.List;

/**
 * Guice module.
 */
public class NiceModule extends AbstractModule {

    private static final Logger log = LoggerFactory.getLogger(NiceModule.class);

    @Override
    protected void configure() {
        bind(Device.class).to(CardTerminalDevice.class);
    }

    @Provides
    @Singleton
    private CardTerminal getCardTerminal() {
        try {
            TerminalFactory terminalFactory = TerminalFactory.getDefault();
            List<CardTerminal> terminals = terminalFactory.terminals().list();
            log.info("Available terminals: {}", terminals);
            CardTerminal cardTerminal = terminals.get(0);
            log.info("Commands will be sent to {}", cardTerminal.getName());
            return cardTerminal;
        } catch (CardException e) {
            DialogFactory.showExceptionDialog(e);
            throw new RuntimeException(e);
        }
    }

    @Provides
    @Singleton
    private DataSource getDataSource() {
        MysqlDataSource dataSource = new MysqlDataSource();
        dataSource.setDatabaseName(Constants.DB_NAME);
        dataSource.setUser(Constants.DB_USER);
        dataSource.setPassword(Constants.DB_PW);
        dataSource.setServerName(Constants.DB_SERVER);
        return dataSource;
    }

}
