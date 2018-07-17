package WebExecuter;

import java.io.IOException;
import java.net.URL;
import java.util.HashMap;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.ComboBox;
import javafx.scene.control.TextField;
import javafx.stage.Modality;
import javafx.stage.Stage;
import javafx.stage.Window;

public class WE_Sub implements Initializable{

	@FXML TextField tf_host, tf_url;
	@FXML Button btnOk, btnCancel, btnAdd;
	@FXML ComboBox<String> comb;

	private final String propName = "WE_Prop.properties";

	private HashMap<String, String> broList = new HashMap<String, String>();
	private WE_Item result; //この値を親画面へ返す

	private Stage primaryStage;
	private FXMLLoader loader;
	private Parent root;
	private Scene scene;

	//ADD
	public WE_Sub( Window owner ) {
		try {
			loader = new FXMLLoader(getClass().getResource("sub.fxml"));
			loader.setController(this);
			root = loader.load();
			scene = new Scene(root);
			primaryStage = new Stage();
			primaryStage.initOwner(owner); //WE_Ctrlerを親として設定
			primaryStage.initModality(Modality.WINDOW_MODAL); //モーダルとして表示
			primaryStage.setScene(scene);
			primaryStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	//EDIT
	public WE_Sub( Window owner, WE_Item target ) {
		try {
			loader = new FXMLLoader(getClass().getResource("sub.fxml"));
			loader.setController(this);
			root = loader.load();
			scene = new Scene(root);
			primaryStage = new Stage();

			setItems( target );  //引数のWE_Itemを入力エリアに追加

			primaryStage.initOwner(owner); //WE_Ctrlerを親として設定
			primaryStage.initModality(Modality.WINDOW_MODAL); //モーダルとして表示
			primaryStage.setScene(scene);
			primaryStage.showAndWait();
		}
		catch (IOException e) {
			e.printStackTrace();
		}
	}

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		init();

		btnOk.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				String host = (tf_host.getText().isEmpty() ? "null" : tf_host.getText());
				String url  = (tf_url.getText().isEmpty() ? "null" : tf_url.getText());

				String key = comb.getSelectionModel().getSelectedItem(); //マップのキーを取得
				String browser = broList.get( key );

				closeAction( new WE_Item( host, url, browser ) );
			}
		});

		btnCancel.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				closeAction( null );
			}
		});
	}

	private void init() {
		Properties conf = readProperties( propName );

		if ( conf != null) {
			broList.put( "default", conf.getProperty("exe_default") ); //デフォルト値だけ別に格納
			comb.getItems().add( "default" );

			String[] bros = conf.getProperty( "exe_list" ).split( "," );
			setWEData( bros );

			comb.getSelectionModel().select( 0 );
		}
	}

	/**.propertiesファイルの読み込み**/
	private Properties readProperties( String name ) {
		Properties conf = null;
		try {
			conf = new Properties();
			conf.load(this.getClass().getResourceAsStream( name ));
		} catch (IOException ex) {
			System.out.println( ex );
		}
		return conf;
	}

	private void setWEData( String[] items ) {
		/*
		 * ～broList にデータを格納～
		 * name[0], url[1], name[2], url[3]の順で格納されるため、
		 * nameを基にURLを取り出すため偶数列のみのインデックスの対象
		 */
		for (int i = 0; i < items.length; i = i + 2) {
			broList.put( items[i], items[i + 1]); //name, url
			comb.getItems().add( items[i] );
		}
	}

	private void setItems( WE_Item target ) {
		tf_host.setText( target.getHost() );
		tf_url.setText( target.getUrl() );
		comb.getSelectionModel().select( getFileName(target.getBrowser()) );
	}

	private String getFileName( String targetPath ) {
		Properties prop = readProperties( propName );
		String[] bros = prop.getProperty( "exe_list" ).split( "," );
		/*
		 * ～broList からキーを取得～
		 * name[0], url[1], name[2], url[3]の順で格納されるため、
		 * urlを基にnameを取り出すため基数列のみインデックスの対象
		 */
		for (int i = 1; i < bros.length; i = i + 2) {
			if ( targetPath.equals( bros[i] ) ) {
				return bros[i - 1];
			}
		}
		return "default";
	}

	/**クローズ処理**/
	private void closeAction( WE_Item item ) {
		this.result = item;
		this.primaryStage.hide();
	}

	private WE_Item returnResult() {
		return this.result;
	}

	public static WE_Item showAndGetValue( Window owner ) {
		WE_Sub sub = new WE_Sub( owner );
		return sub.returnResult();
	}

	public static WE_Item showAndEditValue( Window owner, WE_Item target ) {
		WE_Sub sub = new WE_Sub( owner, target );
		return sub.returnResult();
	}

}
