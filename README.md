# NICE
NICE (NFC-Integrated Cards for Entry) attendance management system.

## Quick Start
Requirements:
- [JDK](http://www.oracle.com/technetwork/java/javase/downloads/index.html) (8u40 or later)
- [Apache Maven](https://maven.apache.org/)
- [MySQL](https://www.mysql.com/)
- [ACR122U USB NFC Reader](https://www.acs.com.hk/en/products/3/acr122u-usb-nfc-reader)

Steps:
1. Connect your ACR122U USB NFC Reader. The device should be showing a red light. If not, ensure that you have the correct drivers installed. On Windows, you may need to start the “Smart Card” service as well.
2. In MySQL, create a database named `nicedb`. Initialize it using [DDL.sql](https://github.com/sudiamanj/NICE/blob/master/src/main/resources/com/sudicode/nice/DDL.sql).
3. Set the following environment variables:

| Variable    | Value                                       |
|-------------|---------------------------------------------|
| `DB_USER`   | Your database username, e.g. `root`         |
| `DB_PW`     | Your database password                      |
| `DB_SERVER` | Location of your database, e.g. `localhost` |
4. Start NICE using the following command:
```bash
mvn install && mvn exec:java
```

### See it in action!
<a href="https://vimeo.com/228209879"><img src="https://raw.githubusercontent.com/sudiamanj/NICE/master/images/demo.png" alt="Video Demo" width="800" height="480"></a>
