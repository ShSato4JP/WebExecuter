package WebExecuter;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.PrintWriter;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Properties;
import java.util.ResourceBundle;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Alert;
import javafx.scene.control.Alert.AlertType;
import javafx.scene.control.Button;
import javafx.scene.control.ButtonType;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.scene.layout.AnchorPane;

public class WE_Ctrler implements Initializable {
	@FXML Button btnAdd, btnDelete, btnEnd, btnEdit;
	@FXML TableView<WE_Item> tv;
	@FXML TableColumn<WE_Item, String> hostCol;
	@FXML TableColumn<WE_Item, String> urlCol;
	@FXML CheckBox chkNo;
	@FXML AnchorPane mainForm;

	private ObservableList<WE_Item> dataList = FXCollections.observableArrayList();
	private WE_Item WE_Data;
	private String exePath = "";
    private String savePath = "";

	@Override
	public void initialize(URL location, ResourceBundle resources) {

		init(); //各コントロールの初期化

		//ADD
		btnAdd.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				addItem();
				writeDataFile(); //データをSystemDataFile.txtに書き込み
			}
		});

		//DELETE
		btnDelete.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				deleteItem();
			}
		});

		//EDIT
		btnEdit.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				editItem();
			}
		});

		//END
		btnEnd.setOnAction( new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent event) {
				close();
			}
		});

		//DOUBLE CLICK : tv
		tv.setOnMouseClicked( new EventHandler<MouseEvent>() {
			@Override
			public void handle(MouseEvent event) {
				boolean doubleClicked = event.getButton().equals( MouseButton.PRIMARY ) &&
						                event.getClickCount() == 2;
				if ( doubleClicked ) {
					doCommand();
				}
			}
		});
	}

	/**初期化処理**/
	private void init() {
		hostCol.setCellValueFactory( new PropertyValueFactory<WE_Item, String>("host") );
		urlCol.setCellValueFactory( new PropertyValueFactory<WE_Item, String>("url") );

		readProperties( "WE_Prop.properties" ); //.propertyファイルの読み込み

		//デフォルトブラウザが設定されていな場合は終了
		if( this.exePath == null ) {

		}

		readDataFile(); //.txtファイルからデータを読み込み

	}

	/**要素追加**/
	private void addItem() {
		if( checkExePath() ) {
			WE_Data = WE_Sub.showAndGetValue( mainForm.getScene().getWindow());
			if ( WE_Data != null ) {
				dataList.add( WE_Data );
				tv.setItems( dataList );
			}
		}
	}

	/**要素削除**/
	private void deleteItem() {
		if( checkExePath() ) {
			if (tv.getSelectionModel().getSelectedItem() != null ){
				int selectIdx = tv.getSelectionModel().getSelectedIndex();
				dataList.remove( selectIdx );
			}
		}
	}

	/**要素編集**/
	private void editItem() {
		if( checkExePath() ) {
			if (tv.getSelectionModel().getSelectedItem() != null ){
				int selectIdx = tv.getSelectionModel().getSelectedIndex();
				WE_Item getData = dataList.get( selectIdx );
				WE_Data = WE_Sub.showAndEditValue( mainForm.getScene().getWindow(), getData);
				if ( WE_Data != null ) {
					dataList.set( selectIdx, WE_Data );
				}
			}
		}
	}

	/**.propertiesファイルの読み込み処理**/
	private void readProperties( String name ) {
		Properties conf;
		try {
			conf = new Properties();
			conf.load(this.getClass().getResourceAsStream( name ));
			this.exePath = conf.getProperty( "exe_default" );
			this.savePath = conf.getProperty( "save_path" );
		} catch (IOException ex) {
			System.out.println( ex );
		}
	}

	/**外部データ(.txt)の読み込み処理**/
	public void readDataFile() {
		String fileName = this.savePath + "SystemDataFile.txt";
		File file = new File( fileName );
		FileReader fr = null;
		BufferedReader br = null;

		//初期化
		dataList.clear();

		//ファイルが存在しない時は新規作成
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			} catch( Exception ex ) {
				System.out.println( ex );
			}
			return;
		}

		//データの読み取り
		try {
			fr = new FileReader( file );
			br = new BufferedReader( fr );

			String line;
			while ( (line = br.readLine()) != null) {
				String[] readData = line.split(",");
				dataList.add( new WE_Item(readData[0], readData[1], readData[2]) ); //[0]:name, [1]:url [2]:browser
			}
		} catch( Exception ex ){
			System.out.println( ex );
		} finally {
			try {
				fr.close();
				br.close();
			} catch(Exception ex ) {
				System.out.println( ex );
			}
		}
		//読み取ったデータを一括で tv に出力
		tv.setItems( dataList );
	}

	public void writeDataFile() {
		String fileName = this.savePath + "SystemDataFile.txt";
		File file = new File( fileName );
		BufferedWriter bw = null;
		PrintWriter pw = null;

		//ファイルが存在しない時は新規作成
		if ( !file.exists() ) {
			try {
				file.createNewFile();
			} catch( Exception ex ) {
				System.out.println( ex );
			}
		}

		try {
			bw = new BufferedWriter( new OutputStreamWriter( new FileOutputStream( file ) , "utf-8" ));
			pw = new PrintWriter( bw );

			//コレクションにデータが格納されている時のみ処理
			if ( 0 < dataList.size() ) {
				for (WE_Item item : dataList) {
					pw.println( item.getSaveData() );
				}
			}

		} catch( Exception ex ){
			System.out.println( ex );
		} finally {
			try {
				bw.close();
				pw.close();
			} catch(Exception ex ){
				System.out.println( ex );
			}
		}
	}

	/**コマンド実行処理**/
	private void doCommand() {
		if (tv.getSelectionModel().getSelectedItem() != null ){
			int selectIdx = tv.getSelectionModel().getSelectedIndex();
			WE_Data = dataList.get( selectIdx );

			if (chkNo.isSelected()) {
				if( !checkCanAccess( WE_Data.getUrl()) ) {
					Alert alert = new Alert( AlertType.NONE,
							                 "サーバーからレスポンスが返ってきませんでした。",
							                 ButtonType.OK );
					alert.setTitle( "ERROR" );
					alert.showAndWait().orElse( ButtonType.OK );
					return;
				}
			}

			//システムコール(コマンドの実行)
			String[] command = null;
			command = new String[2];
			command[0] = ( WE_Data.getBrowser() == null ? exePath : WE_Data.getBrowser());
			command[1] = WE_Data.getUrl();

			Runtime runtime = Runtime.getRuntime();
			try {
				runtime.exec( command );
			} catch( Exception ex ) {
				System.out.println( ex );
			}
		}
	}

	/**相手サイトへアクセスできるか判断**/
	private boolean checkCanAccess( String target ) {
		int status = 0;
		try {
			URL url = new URL( target );
			HttpURLConnection con = ( HttpURLConnection )url.openConnection();
			con.setRequestMethod( "HEAD" );
			con.connect();
			status = con.getResponseCode();
		} catch( Exception ex ) {
			System.out.println( ex );
		}

		if (status == HttpURLConnection.HTTP_OK) {
			return true;
		} else {
			return false;
		}
	}

	/**exe_defaultが設定されているか確認**/
	private boolean checkExePath() {
		if ( this.exePath == null ) {
			Alert alert = new Alert( AlertType.NONE,
					                 "デフォルトブラウザが設定されていません。\n"
					                 + "プロパティファイルをから設定を行ってください。",
                                     ButtonType.OK );
			alert.setTitle( "ERROR" );
			alert.showAndWait().orElse( ButtonType.OK );

			return false;
		}

		return true;
	}

	/**アプリケーション終了**/
	private void close() {
		writeDataFile(); //データをSystemDataFile.txtに書き込み
		mainForm.getScene().getWindow().hide();
	}
}
