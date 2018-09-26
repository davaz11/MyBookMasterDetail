package com.example.dani.mybookmasterdetail.parserXML;
import android.util.Xml;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import java.io.IOException;
import java.io.InputStream;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.HashMap;
import java.util.Locale;


import com.example.dani.mybookmasterdetail.model.BookItem;

/**
 * Parser XML
 */
public class ParserXML {

    // Namespace general. null si no existe
    private static final String ns = null;

    // Constantes del archivo Xml
    private static final String ETIQUETA_HOTELES = "books";

    private static final String ETIQUETA_HOTEL = "book";

    private static final String ETIQUETA_ID_HOTEL = "id";
    private static final String ETIQUETA_NOMBRE = "name";
    private static final String ETIQUETA_DATE = "date";
    private static final String ETIQUETA_PRECIO = "autor";
    private static final String ETIQUETA_VALORACION = "valoracion";
    private static final String ETIQUETA_URL_IMAGEN = "urlImagen";
    private static final String ETIQUETA_DESCRIPCION = "descripcion";

    private static final String PREFIJO = "datosWeb";
    private static final String ATRIBUTO_CALIFICACION = "calificacion";
    private static final String ATRIBUTO_OPINIONES = "noOpiniones";


    /**
     * Parsea un flujo XML a una lista de objetos {@link BookItem}
     *
     * @param in flujo
     * @return Lista de hoteles
     * @throws XmlPullParserException
     * @throws IOException
     */
    public List<BookItem> parsear(InputStream in) throws XmlPullParserException, IOException {
        try {
            XmlPullParser parser = Xml.newPullParser();
            parser.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
            parser.setInput(in, null);
            parser.nextTag();
            List<String> s = new ArrayList<String>();
            return leerHoteles(parser);



        } finally {
            in.close();
        }
    }

    /**
     * Convierte una serie de etiquetas <hotel> en una lista
     *
     * @param parser
     * @return lista de hoteles
     * @throws XmlPullParserException
     * @throws IOException
     */
    private List<BookItem> leerHoteles(XmlPullParser parser)
            throws XmlPullParserException, IOException {

        try {
            List<BookItem> listaHoteles = new ArrayList<BookItem>();

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_HOTELES);
            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String nombreEtiqueta = parser.getName();
                // Buscar etiqueta <hotel>
                if (nombreEtiqueta.equals(ETIQUETA_HOTEL)) {
                    listaHoteles.add(leerHotel(parser));
                } else {
                    saltarEtiqueta(parser);
                }
            }
            return listaHoteles;
        } catch (ParseException e) {
            e.printStackTrace();
            return null;
        }

    }

    /**
     * Convierte una etiqueta <hotel> en un objero Hotel
     *
     * @param parser parser XML
     * @return nuevo objeto Hotel
     * @throws XmlPullParserException
     * @throws IOException
     */
    private BookItem leerHotel(XmlPullParser parser) throws XmlPullParserException, IOException, ParseException {

        try {
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_HOTEL);
            int idHotel = 0;
            String nombre = null;
            String date=null;
            String autor = null;
            float calificacion = 0;
            String urlImagen = null;
            int noOpiniones = 0;
            String descripcion = null;
            HashMap<String, String> valoracion = new HashMap<>();

            while (parser.next() != XmlPullParser.END_TAG) {
                if (parser.getEventType() != XmlPullParser.START_TAG) {
                    continue;
                }
                String name = parser.getName();

                switch (name) {
                    case ETIQUETA_ID_HOTEL:
                        idHotel = leerIdHotel(parser);
                        break;
                    case ETIQUETA_NOMBRE:
                        nombre = leerNombre(parser);
                        break;

                    case ETIQUETA_PRECIO:
                        autor = leerPrecio(parser);
                        break;
                     case ETIQUETA_DATE:
                        date=leerUrlDate(parser);
                        break;
                    case ETIQUETA_VALORACION:
                        valoracion = leerValoracion(parser);
                        calificacion = Float.parseFloat(valoracion.get(ATRIBUTO_CALIFICACION));
                        noOpiniones = Integer.parseInt(valoracion.get(ATRIBUTO_OPINIONES));
                        break;
                    case ETIQUETA_URL_IMAGEN:
                        urlImagen = leerUrlImagen(parser);
                        break;
                    case ETIQUETA_DESCRIPCION:
                        descripcion = leerDescripcion(parser);
                        break;
                    default:
                        saltarEtiqueta(parser);
                        break;
                }
            }

            DateFormat formater = new SimpleDateFormat("dd-MM-yyyy", Locale.ENGLISH);
            Date dt=formater.parse(date);

            //Date dt=Calendar.getInstance(Locale.ENGLISH).getTime();

            return new BookItem(idHotel,
                    nombre,
                    autor,
                    dt,
                    descripcion,
                    urlImagen);
        } catch (IOException e) {
            e.printStackTrace();
            return null;
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return null;
        } catch (NumberFormatException e) {
            e.printStackTrace();
            return null;
        }

    }

    // Procesa la etiqueta <idHotel> de los hoteles
    private int leerIdHotel(XmlPullParser parser) throws IOException, XmlPullParserException {
        parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_ID_HOTEL);
        int idHotel = Integer.parseInt(obtenerTexto(parser));
        parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_ID_HOTEL);
        return idHotel;
    }

    // Procesa las etiqueta <nombre> de los hoteles
    private String leerNombre(XmlPullParser parser) throws IOException, XmlPullParserException {

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_NOMBRE);
            String nombre = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_NOMBRE);
            return nombre;

    }

    // Procesa la etiqueta <precio> de los hoteles
    private String leerPrecio(XmlPullParser parser) throws IOException, XmlPullParserException {

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_PRECIO);
            String precio = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_PRECIO);
            return precio;

    }

    // Procesa la etiqueta <valoracion> de los hoteles
    private HashMap<String, String> leerValoracion(XmlPullParser parser)
            throws IOException, XmlPullParserException {

            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_VALORACION);
            String calificacion = parser.getAttributeValue(null, ATRIBUTO_CALIFICACION);
            String noOpiniones = parser.getAttributeValue(null, ATRIBUTO_OPINIONES);
            parser.nextTag();
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_VALORACION);

            HashMap<String, String> atributos = new HashMap<>();
            atributos.put(ATRIBUTO_CALIFICACION, calificacion);
            atributos.put(ATRIBUTO_OPINIONES, noOpiniones);

            return atributos;

    }

    // Procesa las etiqueta <urlImagen> de los hoteles
    private String leerUrlImagen(XmlPullParser parser) throws IOException, XmlPullParserException {

            String urlImagen;
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_URL_IMAGEN);
            urlImagen = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_URL_IMAGEN);
            return urlImagen;

    }


    private String leerUrlDate(XmlPullParser parser) throws IOException, XmlPullParserException {

        try {
            String date;
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_DATE);
            date = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_DATE);
            return date;
        } catch (IOException e) {
            e.printStackTrace();
            return "";
        } catch (XmlPullParserException e) {
            e.printStackTrace();
            return "";
        }

    }

    // Procesa las etiqueta <descripcion> de los hoteles
    private String leerDescripcion(XmlPullParser parser) throws IOException, XmlPullParserException {

            String descripcion = "";
            parser.require(XmlPullParser.START_TAG, ns, ETIQUETA_DESCRIPCION);
            String prefijo = parser.getPrefix();
            if (prefijo.equals(PREFIJO))
                descripcion = obtenerTexto(parser);
            parser.require(XmlPullParser.END_TAG, ns, ETIQUETA_DESCRIPCION);
            return descripcion;

    }

    // Obtiene el texto de los atributos
    private String obtenerTexto(XmlPullParser parser) throws IOException, XmlPullParserException {
        String resultado = "";
        if (parser.next() == XmlPullParser.TEXT) {
            resultado = parser.getText();
            parser.nextTag();
        }
        return resultado;
    }

    // Salta aquellos objeteos que no interesen en la jerarquía XML.
    private void saltarEtiqueta(XmlPullParser parser) throws XmlPullParserException, IOException {
        if (parser.getEventType() != XmlPullParser.START_TAG) {
            throw new IllegalStateException();
        }
        int depth = 1;
        while (depth != 0) {
            switch (parser.next()) {
                case XmlPullParser.END_TAG:
                    depth--;
                    break;
                case XmlPullParser.START_TAG:
                    depth++;
                    break;
            }
        }
    }

}