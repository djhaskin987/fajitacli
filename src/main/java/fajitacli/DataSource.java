package fajitacli;

import java.util.List;
import java.math.BigDecimal;
import java.math.BigInteger;
import com.fasterxml.jackson.core.JsonParser;

public interface DataSource {

    List<String> optionNames();

    Boolean consumeAsFlag(String optionName);

    String consumeAsString(String optionName);

    BigDecimal consumeAsDecimal(String optionName);

    BigInteger consumeAsInteger(String optionName);

    List<BigDecimal> consumeAsListOfDecimal(String optionName);

    List<BigInteger> consumeAsLiftOfInteger(String optionName);

    List<String> consumeAsListOfString(String optionName);

    JsonParser consumeAsJson(String optionName);

    JsonParser consumeAsFile(String optionName);

    boolean optionsLeft();

}
