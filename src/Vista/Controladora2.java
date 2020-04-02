package Vista;

import java.sql.SQLException;
import java.util.Optional;

import Modelo.ControladoraBBDD;
import Modelo.Persona;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.RadioButton;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.ToggleGroup;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.cell.PropertyValueFactory;

public class Controladora2 {

	@FXML
	private Button btnGrabar;

	@FXML
	private Button btnBorrar;

	@FXML
	private Button btnEliminar;

	@FXML
	private Button btnBuscar;
	
	@FXML
	private CheckBox chkcasado;

	@FXML
	RadioButton hombre;

	@FXML
	RadioButton mujer;

	@FXML
	ToggleGroup sexo;

	@FXML
	private TableView<Persona> tabla;

	@FXML
	private TableColumn<Persona,String> col_nombre;

	@FXML
	private TableColumn<Persona,String> col_apellido;

	@FXML
	private TableColumn<Persona,String> col_email;

	@FXML
	private TableColumn<Persona,Character> col_sexo;

	@FXML
	private TableColumn<Persona,Boolean> col_casado;

	@FXML
	private TextField txtNombre;

	@FXML
	private TextField txtApellido;

	@FXML
	private TextField email;
	
	@FXML
	private TextField txtBuscar;

	ObservableList<Persona> datos = FXCollections.observableArrayList();
	ObservableList<Persona> datos2 = FXCollections.observableArrayList();
	ObservableList<Persona> datosBusqueda = FXCollections.observableArrayList();

	// Atributos necesarios para codificar la edicion
	private boolean edicion;
	private int indiceedicion;

	ControladoraBBDD con;

	public void initialize() throws SQLException{
		
		
		con = new ControladoraBBDD ();
		
		datos = con.ConsultaPersonas();
		tabla.setItems(datos);
		
		col_nombre.setCellValueFactory(new PropertyValueFactory<Persona,String>("Nombre"));
		col_apellido.setCellValueFactory(new PropertyValueFactory<Persona,String>("Apellido"));
		col_email.setCellValueFactory(new PropertyValueFactory<Persona,String>("email"));
		col_sexo.setCellValueFactory(new PropertyValueFactory<Persona,Character>("sexo"));
		col_casado.setCellValueFactory(new PropertyValueFactory<Persona,Boolean>("casado"));

		// Al arrancar la vista se pone edicion a false
		edicion = false;
		indiceedicion = 0;

	}

	public void Guardar() throws SQLException{

		con = new ControladoraBBDD ();
		
		boolean casado = chkcasado.isSelected();
		char sexo;

		if(hombre.isSelected())
			sexo = 'H';
		else
			sexo = 'M';

		// Añadir un chequeo de campos vacíos o de validación de formato como el email
		if(txtNombre.getText().length()==0 || txtApellido.getText().length()==0 || email.getText().length()==0){
			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error!!!");
			alert.setHeaderText("Observa que hayas introducido todos los datos");
			alert.setContentText("¡No se pueden grabar campos vacíos!");
			alert.showAndWait();
		}
		else{

    			Persona nueva = new Persona(txtNombre.getText(),txtApellido.getText(),email.getText(),sexo,casado);
    			datos2.add(nueva);
    		
    			if(edicion == true){
    				
    				Persona editada = datos.get(indiceedicion);
    				editada.setNombre(txtNombre.getText());
    				editada.setApellido(txtApellido.getText());
    				editada.setEmail(email.getText());
    				editada.setCasado(casado);
    				editada.setSexo(sexo);
    				datos.set(indiceedicion, editada);
    				
    				if(datos.contains(email.getText())==true){
   	      	 	     Alert alert = new Alert (Alert.AlertType.ERROR);
   	      	 	     alert.setTitle("ERROR.Persona existente.");
   	      	 		 alert.setHeaderText(null);
   	      	 		 alert.setContentText("Esta persona ya existe, o la clave 'Email' ya existe para otra persona.");
   	      	 		 alert.showAndWait();
   				     }else {
   				    	con.modificarPersona(datos2.get(0),editada);
					}
    				
    			}
    			else{
    				Persona nuevo = new Persona(txtNombre.getText(),txtApellido.getText(),email.getText(),sexo,casado);
    				boolean emailtest = false;
    				for(int i=0;i<datos.size();i++){
    					if(datos.get(i).getEmail().equals(nuevo.getEmail())){
    						emailtest = true;
    					}
    				}
    				if(emailtest==true){
      	      	 	     Alert alert = new Alert (Alert.AlertType.ERROR);
       	      	 	     alert.setTitle("ERROR.Persona existente.");
       	      	 		 alert.setHeaderText(null);
       	      	 		 alert.setContentText("Esta persona ya existe, o la clave 'Email' ya existe para otra persona.");
       	      	 		 alert.showAndWait();
    				}else {
        				con.guardarPersona(nuevo);
        				datos.add(nuevo);
					}
    			
    			}
		}
	}

	public void Eliminar() throws SQLException{
		
		con = new ControladoraBBDD ();
		
		int index = tabla.getSelectionModel().getSelectedIndex();
		if( index >= 0){

			Persona seleccionada = tabla.getSelectionModel().getSelectedItem();

			// Se abre un dialog box de confirmacion de eliminar
			Alert alert = new Alert(AlertType.CONFIRMATION);
			alert.setTitle("Conformación!!!");
			alert.setHeaderText("Por favor confirme el borrado");
			alert.setContentText("Dese borrar al usuario "+ seleccionada.getNombre() + " " +seleccionada.getApellido() +" ?");

			Optional<ButtonType> result = alert.showAndWait();
			if (result.get() == ButtonType.OK){
			    // ... user chose OK
				con.eliminarPersona(seleccionada);
				datos.remove(seleccionada);
				tabla.setItems(this.datos);

				Borrar();
			}

		}else{

			Alert alert = new Alert(AlertType.ERROR);
			alert.setTitle("Error");
			alert.setHeaderText("Error en selección de datos");
			alert.setContentText("NO HAY NINGUN ELEMENTO SELECCIONADO!");
			alert.showAndWait();

		}
	}

	public void Borrar(){
		txtNombre.setText("");
		txtApellido.setText("");
		email.setText("");
		chkcasado.setSelected(false);

		edicion = false;
		indiceedicion = 0;
	}

	public void Editar() throws SQLException{

		int index = tabla.getSelectionModel().getSelectedIndex();


		if( index >= 0){

			// Activo la "funcionalidad" de editar para luego que el botón guardar sepa a qué PErsona estoy "editando"
			edicion = true;
			indiceedicion = index;


			Persona seleccionada = tabla.getSelectionModel().getSelectedItem();

			txtNombre.setText(seleccionada.getNombre());
			txtApellido.setText(seleccionada.getApellido());
			email.setText(seleccionada.getEmail());
			chkcasado.setSelected(seleccionada.isCasado());
			if(seleccionada.getSexo() == 'H')
				hombre.setSelected(true);
			else
				mujer.setSelected(true);

		}
	}
	public void Buscar() throws SQLException{
		
		if(this.txtBuscar.getText().length()==0){
			con = new ControladoraBBDD ();
			
			datos = con.ConsultaPersonas();
			tabla.setItems(datos);
			
			col_nombre.setCellValueFactory(new PropertyValueFactory<Persona,String>("Nombre"));
			col_apellido.setCellValueFactory(new PropertyValueFactory<Persona,String>("Apellido"));
			col_email.setCellValueFactory(new PropertyValueFactory<Persona,String>("email"));
			col_sexo.setCellValueFactory(new PropertyValueFactory<Persona,Character>("sexo"));
			col_casado.setCellValueFactory(new PropertyValueFactory<Persona,Boolean>("casado"));

			edicion = false;
			indiceedicion = 0;
		}else {
			con = new ControladoraBBDD();
			con.buscarPersona(this.txtBuscar.getText());
			datosBusqueda = con.buscarPersona(this.txtBuscar.getText());
			tabla.setItems(datosBusqueda);
		}
		
	}

}
