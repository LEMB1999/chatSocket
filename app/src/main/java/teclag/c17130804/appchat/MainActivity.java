package teclag.c17130804.appchat;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import android.util.Log;
import android.view.Gravity;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;
import java.io.BufferedReader;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.net.ServerSocket;
import java.net.Socket;

public class MainActivity extends AppCompatActivity {


    private Button btnSend;
    private Socket client;
    private ServerSocket server;

    private View Datos;
    EditText Servidor,Usuario,mensaje;
    LinearLayout chat;

    String strServidor,strUsuario;

    private PrintWriter output;
    private BufferedReader input;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Datos = getLayoutInflater().inflate( R.layout.datos ,null);
        chat = findViewById(R.id.chatText);
        mensaje = findViewById(R.id.mensaje);

        AlertDialog.Builder alert = new AlertDialog.Builder(this);
        alert.setTitle("Datos").setView(Datos).setIcon( R.drawable.logomaniacos).setPositiveButton("Entrar ", (dialog, which) -> {

            Servidor = Datos.findViewById(R.id.edtServidor);
            Usuario = Datos.findViewById(R.id.edtUsuario);

            strServidor = Servidor.getText().toString();
            strUsuario = Usuario.getText().toString();


            new Thread(new Servidor()).start();

        }).setCancelable(false).create().show();

        btnSend = findViewById(R.id.btnSend);
        btnSend.setOnClickListener(v -> {

            new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        client = new Socket(strServidor,1500);
                        output = new PrintWriter(client.getOutputStream());
                        String Message = mensaje.getText().toString();
                        output.write(strUsuario + ":" + Message);
                        output.flush();
                        client.close();

                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtMensaje = new TextView(MainActivity.this);
                                txtMensaje.setText(Message);
                                txtMensaje.setGravity(Gravity.RIGHT);
                                txtMensaje.setTextSize(20);
                                chat.addView(txtMensaje);
                                mensaje.setText("");
                                mensaje.requestFocus();
                            }
                        });

                    } catch (IOException e) {
                        e.printStackTrace();
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                Toast.makeText(MainActivity.this,"Error al enviar el mensaje",Toast.LENGTH_LONG).show();
                            }
                        });
                    }

                }
            }).start();
       });
    }



    @Override
    protected void onDestroy() {
        super.onDestroy();
        try {
            client.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    class Servidor implements  Runnable {
        @Override
        public void run() {
            try {
                server = new ServerSocket(1500);
            } catch (IOException e) {
                e.printStackTrace();
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(MainActivity.this,"Error al iniciar el servidor",Toast.LENGTH_LONG).show();
                    }
                });
            }
            while(true) {
                try {
                    Socket cliente = server.accept();
                     input = new BufferedReader(new InputStreamReader(cliente.getInputStream()));
                     String mensaje = input.readLine();
                    if(mensaje != null && !mensaje.isEmpty()){
                        runOnUiThread(new Runnable() {
                            @Override
                            public void run() {
                                TextView txtMensaje = new TextView(MainActivity.this);
                                txtMensaje.setText(mensaje);
                                txtMensaje.setTextSize(20);
                                txtMensaje.setGravity(Gravity.LEFT);
                                chat.addView(txtMensaje);
                            }
                        });
                    }
                    cliente.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
             }
        }
    }

}//cierre de la clase principal