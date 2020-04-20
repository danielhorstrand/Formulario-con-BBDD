package Modelo;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Properties;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ControladoraBBDD {

	private String url= "";
	private String user = "";
	private String pwd = "";
	private String usr = "";
	private Connection conexion;
	
	public ControladoraBBDD()  {

		Properties propiedades = new Properties();
		InputStream entrada = null;
		
		try {
			File miFichero = new File("src/Modelo/datos.ini");
			if (miFichero.exists()){
				System.out.println("entra");
				entrada = new FileInputStream(miFichero);
				propiedades.load(entrada);
				url=propiedades.getProperty("url");
				user=propiedades.getProperty("user");
				pwd=propiedades.getProperty("pwd");
				usr=propiedades.getProperty("usr");
			}

			else
				System.out.println("Fichero no encontrado");
		} catch (IOException ex) {
			ex.printStackTrace();
		}finally {
			if (entrada != null) {
				try {
					entrada.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conexion = DriverManager.getConnection(url, user, pwd);

			if(conexion.isClosed())
				System.out.println("Fallo en Conexión con la Base de Datos");


		}catch (Exception e) {
			System.out.println("ERROR en conexión con ORACLE");
			e.printStackTrace();
		}
	}
		
	public ObservableList<Persona>  ConsultaPersonas() throws SQLException{
		//Preparo la conexión para ejecutar sentencias SQL de tipo update

		ObservableList<Persona> listaPersonas =  FXCollections.observableArrayList();
		
		Statement stm = conexion.createStatement();
		String selectsql = "SELECT NOMBRE, APELLIDO, EMAIL, SEXO, CASADO FROM "+usr+".PERSONAS";

		ResultSet resultado = stm.executeQuery(selectsql);
	
		try{
			while (resultado.next()) {
				String nombre = resultado.getString(1);
				String apellido = resultado.getString(2);
				String email = resultado.getString(3);
				char sexo = resultado.getString(4).charAt(0);
				boolean casado = false;
				if(resultado.getString(5).equals("true")){
					casado = true;
				}
				Persona a = new Persona (nombre,apellido,email,sexo,casado);
				listaPersonas.add(a);
			}
			
		}catch(SQLException sqle){
			
			int pos = sqle.getMessage().indexOf(":");
			String codeErrorSQL = sqle.getMessage().substring(0,pos);

			System.out.println(codeErrorSQL);
		}
		return listaPersonas;
	}
	public void guardarPersona (Persona persona)  throws SQLException{
		
		Statement stm = conexion.createStatement();
		
		try{
			String insertsql = "INSERT INTO "+usr+".PERSONAS VALUES (?,?,?,?,?)";

			String auxcasado = "N";
			if(persona.isCasado() == true){
				auxcasado = "S";
			}
			PreparedStatement pstmt = conexion.prepareStatement (insertsql);
			pstmt.setString(1, persona.getNombre());
			pstmt.setString(2, persona.getApellido());
			pstmt.setString(3, persona.getEmail());
			pstmt.setString(4, Character.toString(persona.getSexo()));
			pstmt.setString(5, auxcasado);
			
			int resultado = pstmt.executeUpdate();

			if(resultado != 1){
				System.out.println("Error en la inserción " + resultado);
		}else{
				System.out.println("Persona insertada con éxito!!!");
		}
		}catch(SQLException sqle){

			int pos = sqle.getMessage().indexOf(":");
			String codeErrorSQL = sqle.getMessage().substring(0,pos);
			System.out.println(codeErrorSQL);

			if(codeErrorSQL.equals("ORA-00001") )
				System.out.println("ERROR.La persona que intentas introducir ya existe, o su clave 'Email' ya esta inscrita!");
			else
				System.out.println("Ha habido algún problema con  Oracle al hacer la creación de tabla");
		}

	}
	public void eliminarPersona (Persona persona)  throws SQLException{
		
		Statement stm = conexion.createStatement();
		
		try{
			String insertsql = "DELETE "+usr+".PERSONAS WHERE EMAIL=?";
			
			PreparedStatement pstmt = conexion.prepareStatement (insertsql);
			
			pstmt.setString(1, persona.getEmail());

			int resultado = pstmt.executeUpdate();

			if(resultado != 1){
				System.out.println("Error en la inserción " + resultado);
		}else{
				System.out.println("Persona borrada con éxito!!!");
		}
		}catch(SQLException sqle){

			int pos = sqle.getMessage().indexOf(":");
			String codeErrorSQL = sqle.getMessage().substring(0,pos);

			System.out.println(codeErrorSQL);
		}
	}
	public void modificarPersona (Persona persona1,Persona persona2)  throws SQLException{
		
		Statement stm = conexion.createStatement();

		try{
			String insertsql = "UPDATE "+usr+".PERSONAS SET NOMBRE=?, APELLIDO =?, EMAIL =?, SEXO =?, CASADO =? WHERE EMAIL=?";

			String auxcasado = "N";
			if(persona2.isCasado() == true){
				auxcasado = "S";
			}
			PreparedStatement pstmt = conexion.prepareStatement (insertsql);
			pstmt.setString(1, persona2.getNombre());
			pstmt.setString(2, persona2.getApellido());
			pstmt.setString(3, persona2.getEmail());
			pstmt.setString(4, Character.toString(persona2.getSexo()));
			pstmt.setString(5, auxcasado);
			pstmt.setString(6, persona1.getEmail());
			
			int resultado = pstmt.executeUpdate();

			if(resultado != 1){
				System.out.println("Error en la modificacion " + resultado);
		}else{
				System.out.println("Persona actualizada con éxito!!!");
		}
		}catch(SQLException sqle){

			int pos = sqle.getMessage().indexOf(":");
			String codeErrorSQL = sqle.getMessage().substring(0,pos);

			System.out.println(codeErrorSQL);
		}
	
	}
	public ObservableList<Persona> buscarPersona (String apellidoBusqueda) throws SQLException {
		
		ObservableList<Persona> listaPersonas =  FXCollections.observableArrayList();
		Statement stm = conexion.createStatement();
		
		String selectsql = "SELECT NOMBRE, APELLIDO, EMAIL, SEXO, CASADO FROM "+usr+".PERSONAS WHERE APELLIDO=?";

		PreparedStatement pstmt = conexion.prepareStatement (selectsql);
		pstmt.setString(1, apellidoBusqueda);
		
		ResultSet resultado = pstmt.executeQuery();
	
		try{
			while (resultado.next()) {
				String nombre = resultado.getString(1);
				String apellido = resultado.getString(2);
				String email = resultado.getString(3);
				char sexo = resultado.getString(4).charAt(0);
				boolean casado = false;
				if(resultado.getString(5).equals("true")){
					casado = true;
				}
				Persona a = new Persona (nombre,apellido,email,sexo,casado);
				listaPersonas.add(a);
			}
			
		}catch(SQLException sqle){
			
			int pos = sqle.getMessage().indexOf(":");
			String codeErrorSQL = sqle.getMessage().substring(0,pos);

			System.out.println(codeErrorSQL);
		}
		return listaPersonas;
	}
}
