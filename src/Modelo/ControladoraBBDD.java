package Modelo;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

public class ControladoraBBDD {

	private String bd;
	private String url= "jdbc:oracle:thin:@localhost:1521:XE";
	private String usr = "SYSTEM";
	private String pwd = "Miro_5838";
	private Connection conexion;
	
	public ControladoraBBDD()  {

		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			conexion = DriverManager.getConnection(url, usr, pwd);

			if(!conexion.isClosed()) {
				System.out.println("Conexión establecida");

				//conexion.close();
			}
			else
				System.out.println("Fallo en Conexión");


		}catch (Exception e) {
			System.out.println("ERROR en conexión con ORACLE");
			e.printStackTrace();
		}

		
	}
	public ObservableList<Persona>  ConsultaPersonas() throws SQLException{
		//Preparo la conexión para ejecutar sentencias SQL de tipo update

		ObservableList<Persona> listaPersonas =  FXCollections.observableArrayList();
		
		Statement stm = conexion.createStatement();
		String selectsql = "SELECT NOMBRE, APELLIDO, EMAIL, SEXO, CASADO FROM DANIEL.PERSONAS";

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
			String insertsql = "INSERT INTO DANIEL.PERSONAS VALUES ('" + persona.getNombre() + "','" + persona.getApellido() + "','" + persona.getEmail() + "','" + persona.getSexo() + "','" + persona.isCasado() + "')";

			int resultado = stm.executeUpdate(insertsql);

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
			String insertsql = "DELETE DANIEL.PERSONAS WHERE EMAIL='" + persona.getEmail() + "'";

			int resultado = stm.executeUpdate(insertsql);

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
			String insertsql = "UPDATE DANIEL.PERSONAS SET NOMBRE='"+persona2.getNombre()+"', APELLIDO ='"+persona2.getApellido()+"', EMAIL ='"+persona2.getEmail()+"', SEXO ='"+persona2.getSexo()+"', CASADO ='"+ persona2.isCasado()+"' WHERE EMAIL='" + persona1.getEmail() + "'";

			int resultado = stm.executeUpdate(insertsql);

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
		String selectsql = "SELECT NOMBRE, APELLIDO, EMAIL, SEXO, CASADO FROM DANIEL.PERSONAS WHERE APELLIDO='"+apellidoBusqueda+"'";

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
}
