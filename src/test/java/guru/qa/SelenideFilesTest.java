package guru.qa;

import com.codeborne.pdftest.PDF;
import com.codeborne.xlstest.XLS;
import com.opencsv.CSVReader;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.List;
import java.util.zip.ZipEntry;
import java.util.zip.ZipInputStream;

import static com.codeborne.selenide.Condition.text;
import static com.codeborne.selenide.Selectors.byText;
import static com.codeborne.selenide.Selenide.$;
import static com.codeborne.selenide.Selenide.open;
import static org.assertj.core.api.Assertions.assertThat;

public class SelenideFilesTest {


    @Test
                /*Класс File — абстракция над путем к файлу в папке в памяти машины. Для чтения/записи существует понятие InputStream и OutputStream соответственно. Для чтения необходимо создать новый InputStream и передать ему файл.
    Далее из файла надо получить массив байт и декодировать его в необходимый стандарт.
     По завершении поток надо обязательно закрыть.*/
        // скачать файл
    void downloadFileTest() throws Exception { // throws Exception, обработка исключений для  File download = $("#raw-url").download();,
        // без него .download(); красный
        open("https://github.com/selenide/selenide/blob/master/README.md");
        File download = $("#raw-url").download();
        //  System.out.println();
        String result; // 27, положили в переменную

        // InputStream - отвечает за чтение в файле
        try (InputStream is = new FileInputStream(download)) { // получаем на вход файл и ищем путь, если есть путь - можем его прочитать
            result = new String(is.readAllBytes(), "UTF-8");
        }
        // проверка в заголовке
        assertThat(result).contains("Selenide = UI Testing Framework powered by Selenium WebDriver");
    }

    //
    @Test
    // загрузить файл
    // input[type='file'], ищем по локатору этому
    void uploadFileTest() {
        open("https://the-internet.herokuapp.com/upload");
        // поиск по проекту в src
        $("input[type='file']").uploadFromClasspath("example.txt");
        $("#file-submit").click();
        $("#uploaded-files")
                .shouldHave(text("example.txt")); // проверка
    }


    @Test
            /*Для работы с PDF-файлами нам понадобится подключить стороннюю библиотеку.
        Для этого в gradle-файле необходимо в раздел dependencies необходимо добавить следующую строку:
         */
/*
dependencies {
    testImplementation (
        'com.codeborne:pdf-test:1.7.0'
    )
}*/
        /*Для открытия PDF-файла необходимо использовать ClassLoader. Это поможет сохранить независимость от файловой системы.
         Далее необходимо создать новый класс PDF и передать поток.*/
    void downloadPdfTest() throws Exception {
        open("https://junit.org/junit5/docs/current/user-guide/");
        File download = $(byText("PDF download")).download();
        PDF parsed = new PDF(download); // класс библиотеки с PDF
        assertThat(parsed.author).contains("Marc Philipp"); // проверка автора
    }

    @Test
    /*Работа с XLS-файлами схожа с работой с PDF. Сперва надо подключить библиотеку:
    * dependencies {
    testImplementation (
        'com.codeborne:xls-test:1.5.0'
    )
}
*
    * */
        //ДОНАСТРОИТЬ   class file for org.apache.poi.ss.usermodel.Workbook not found   'org.apache.poi:poi-ooxml:4.0.0'
    void downloadExcelTest() throws Exception {
        //       Файлы открываются следующим образом(2 стороки):
        try (InputStream stream = getClass().getClassLoader().getResourceAsStream("175.xlsx")) { // сначала нужно скачать и положить в папку ресурсы
            XLS parsed = new XLS(stream);
            /*Обращаться к XLS-файлам чуть сложнее. Сперва вспомним какие элементы есть в таблицах:

Листы — их может быть несколько в одной таблице;
Столбцы;
Строки;
Ячейки.
К каждому элементу можно обратиться с помощью вызова метода:

листы — getSheetAt();
строчки — getRow();
столбцы — getCell();
ячейка — пересечение строки и столбца.
Пример записи строки из первого листа, третьей строки и первого столбца в переменную (нумерация начинается с нуля):*/
            assertThat(parsed.excel.getSheetAt(1).getRow(4).getCell(1).getStringCellValue())
                    .isEqualTo("Алешина Ольга Валентиновна");
        }
    }

    @Test
    /*CSV (Comma-Separated Values) — текстовый формат, предназначенный для представления табличных данных.
    Строка таблицы соответствует строке текста, которая содержит одно или несколько полей, разделенных запятыми.
    'com.opencsv:opencsv:5.6'

    */
        // не правильно был создан файл
    void parseCsvTest() throws Exception {
        URL url = getClass().getClassLoader().getResource("file.csv");
        CSVReader reader = new CSVReader(new FileReader(new File(url.toURI())));

        List<String[]> strings = reader.readAll();

        assertThat(strings).contains( // првоеряем содержимое
                new String[]{"lector"}//,
         /*       new String[] {"Tuchs", "JUnit"},
                new String[] {"Eroshenko", "Allure"}*/
        );
    }


    @Test
        /*ZIP-архивы открываются практически идентичным образом. На примере разберем открытие такого архива и проверку его содержимого:
         * В архиве может находиться много разных файлов и для удобной работы с ними есть объект ZipEntry.
         * Цикл while необходим для перебора содержимого архива.*/
    void parseZipFileTest() throws Exception {
        try (InputStream is = getClass().getClassLoader().getResourceAsStream("zip_2MB.zip")) {
            ZipInputStream zis = new ZipInputStream(is);
            ZipEntry entry;

            while ((entry = zis.getNextEntry()) != null) {
                System.out.println(entry.getName());

                /* //           если бы в архиве был текстовый файл
//            Scanner sc = new Scanner(zis);
//            while (sc.hasNext()) {
//                System.out.println(sc.nextLine());*/
            }
        }
    }
}

