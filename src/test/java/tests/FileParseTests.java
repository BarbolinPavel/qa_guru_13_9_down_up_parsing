package tests;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static java.nio.charset.StandardCharsets.UTF_8;
import static org.assertj.core.api.AssertionsForInterfaceTypes.assertThat;


public class FileParseTests {
    ClassLoader classLoader = FileParseTests.class.getClassLoader();

    @DisplayName("Парсинг файлов в zip архиве")
    @Test
    void zipTest() throws Exception{
        String value1 = ".pdf";
        String value2 = ".xlsx";
        String value3 = ".csv";

        try(InputStream is = classLoader.getResourceAsStream("Example.zip")){
            try(ZipInputStream zis = new ZipInputStream(is)) {
                ZipEntry entry;
                while ((entry = zis.getNextEntry()) != null) {
                    if(entry.getName().contains(value1)){
                        PDF pdf = new PDF(zis);
                        assertThat(pdf.text).contains("PDF");
                    } else if (entry.getName().contains(value2)) {
                        XLS xls = new XLS(zis);
                        assertThat(xls.excel.getSheetAt(0)
                                .getRow(4)
                                .getCell(3)
                                .getStringCellValue()
                        ).contains("Джефф");
                    } else if (entry.getName().contains(value3)){
                        CSVReader csvReader = new CSVReader(new InputStreamReader(zis, UTF_8));
                            List<String[]> csv = csvReader.readAll();
                            assertThat(csv).contains(
                            new String[]{"team","country","date"},
                            new String[]{"Juventus","Italy","1897"});
                    }
                }
            }
        }
    }

    @DisplayName("Парсинг json с помощью библиотеки Jackson")
    @Test
    void jsonJacksonTest() throws Exception{
        try (InputStream is = classLoader.getResourceAsStream("car.json")) {
            ObjectMapper objectMapper = new ObjectMapper();
            JsonNode jsonNode = objectMapper.readTree(new InputStreamReader(is, UTF_8));

            assertThat(jsonNode.get("year").asInt()).isEqualTo(2006);
            assertThat(jsonNode.get("color").asText()).isEqualTo("black");
            assertThat(jsonNode.get("luke").asBoolean()).isEqualTo(true);
            assertThat(jsonNode.get("consumption").get("city").asDouble()).isEqualTo(8.7);
        }
    }
}

