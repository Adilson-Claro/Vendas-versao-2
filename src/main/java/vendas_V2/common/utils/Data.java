package vendas_V2.common.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Data {

    public static String formatarData(LocalDate data) {
        if (data != null) {
            var dataFormat = DateTimeFormatter.ofPattern("dd/MM/yyyy");
            return data.format(dataFormat);
        }
        return null;
    }
}
