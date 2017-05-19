package util;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import java.io.File;
import java.io.IOException;

public class DownloadHistoryUtil {

    public static void beanToXml(File xmlFile, Object object, Class<?> beanClass) throws JAXBException {
        Marshaller marshaller = JAXBContext.newInstance(beanClass).createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(object, xmlFile);
        marshaller.marshal(object, System.out);
    }

    public static Object xmlToBean(File xmlFile, Class<?> beanClass) throws JAXBException, IOException {
        return JAXBContext.newInstance(beanClass).createUnmarshaller().unmarshal(xmlFile);
    }

}
