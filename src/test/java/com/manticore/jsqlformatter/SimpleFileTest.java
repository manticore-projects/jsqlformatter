/**
 * Manticore Projects JSQLFormatter is a SQL Beautifying and Formatting Software.
 * Copyright (C) 2022 Andreas Reichel <andreas@manticore-projects.com>
 * <p>
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU Affero General Public License as published
 * by the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 * <p>
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU Affero General Public License for more details.
 * <p>
 * You should have received a copy of the GNU Affero General Public License
 * along with this program.  If not, see <https://www.gnu.org/licenses/>.
 */
package com.manticore.jsqlformatter;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedHashMap;
import java.util.Map.Entry;
import java.util.Objects;
import java.util.logging.Level;
import java.util.stream.Stream;

/**
 * @author Andreas Reichel <andreas@manticore-projects.com>
 */
public class SimpleFileTest extends StandardFileTest {

    public final static String TEST_FOLDER_STR = "build/resources/test/com/manticore/jsqlformatter/simple";

    public static Stream<Entry<SQLKeyEntry, String>> getSqlMap() {
        LinkedHashMap<SQLKeyEntry, String> sqlMap = new LinkedHashMap<>();

        for (File file : Objects.requireNonNull(new File(TEST_FOLDER_STR).listFiles(FILENAME_FILTER))) {
            StringBuilder stringBuilder = new StringBuilder();
            String line;
            String k = "";

            try (FileReader fileReader = new FileReader(file);
                 BufferedReader bufferedReader = new BufferedReader(fileReader)
            ) {
                while ((line = bufferedReader.readLine()) != null) {
                    stringBuilder.append(line).append("\n");
                }
                sqlMap.put(new SQLKeyEntry(file, k), stringBuilder.toString().trim());
            } catch (IOException ex) {
                LOGGER.log(Level.SEVERE, "Failed to read " + file.getAbsolutePath(), ex);
            }
        }

        return sqlMap.entrySet().stream();
    }
}
