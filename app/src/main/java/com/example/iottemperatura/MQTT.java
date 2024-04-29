package com.example.iottemperatura;

import android.content.Context;
import android.widget.TextView;
import android.widget.Toast;

import org.eclipse.paho.android.service.MqttAndroidClient;
import org.eclipse.paho.client.mqttv3.IMqttActionListener;
import org.eclipse.paho.client.mqttv3.IMqttDeliveryToken;
import org.eclipse.paho.client.mqttv3.IMqttToken;
import org.eclipse.paho.client.mqttv3.MqttCallback;
import org.eclipse.paho.client.mqttv3.MqttClient;
import org.eclipse.paho.client.mqttv3.MqttConnectOptions;
import org.eclipse.paho.client.mqttv3.MqttException;
import org.eclipse.paho.client.mqttv3.MqttMessage;

public class MQTT {

    private MqttAndroidClient mqttAndroidClient;
    private TextView tvTemperatura;
    private TextView tvUmidade;

    public void conectar(Context context, TextView textViewTemperatura, TextView textViewUmidade) {
        String brokerUrl = "tcp://broker.hivemq.com:1883";
        String usuario = "";
        String senha = "";

        tvTemperatura = textViewTemperatura;
        tvUmidade = textViewUmidade;

        MqttConnectOptions options = new MqttConnectOptions();
        options.setUserName(usuario);
        options.setPassword(senha.toCharArray());

        mqttAndroidClient = new MqttAndroidClient(context, brokerUrl, MqttClient.generateClientId());

        try {
            mqttAndroidClient.connect(options, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "Sucesso ao conectar no MQTT", Toast.LENGTH_LONG).show();
                    receberDados(context);
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Erro ao conectar no MQTT", Toast.LENGTH_LONG).show();
                }
            });
        } catch (MqttException e) {
            Toast.makeText(context, "Exception ao conectar no MQTT", Toast.LENGTH_LONG).show();
        }
    }

    public void desconectar(Context context) {
        try {
            mqttAndroidClient.disconnect();
            Toast.makeText(context, "Desconectado do MQTT com sucesso", Toast.LENGTH_LONG).show();
        } catch (MqttException e) {
            Toast.makeText(context, "Exception ao conectar no MQTT", Toast.LENGTH_LONG).show();
        }
    }

    private void receberDados(Context context) {
        int qos = 0;
        String topico = "senai_temp_umid";
        try {
            mqttAndroidClient.subscribe(topico, qos, null, new IMqttActionListener() {
                @Override
                public void onSuccess(IMqttToken asyncActionToken) {
                    Toast.makeText(context, "Inscrito no tópico para leitura de dados", Toast.LENGTH_LONG).show();
                }

                @Override
                public void onFailure(IMqttToken asyncActionToken, Throwable exception) {
                    Toast.makeText(context, "Erro ao inscrever-se no tópico para leitura de dados", Toast.LENGTH_LONG).show();
                }
            });

            mqttAndroidClient.setCallback(new MqttCallback() {
                @Override
                public void connectionLost(Throwable cause) {
                    Toast.makeText(context, "Conexão foi encerrada pelo broker", Toast.LENGTH_LONG).show();
                }

                @Override
                public void messageArrived(String topic, MqttMessage message) throws Exception {
                    String dados = new String(message.getPayload());
                    Toast.makeText(context, "Mensagem recebida: " + dados, Toast.LENGTH_LONG).show();
                    exibindoDadosTela(dados);
                }

                @Override
                public void deliveryComplete(IMqttDeliveryToken token) {

                }
            });
        } catch (MqttException e) {
            Toast.makeText(context, "Erro ao inscrever-se no tópico para receber dados de temperatura e umidade", Toast.LENGTH_LONG).show();
        }
    }

    private void exibindoDadosTela(String dados) {
        // Separando os valores
        String[] valores = dados.split("\\|");

        // Atribuindo os valores separados às variáveis
        String temperatura = valores[0];
        String umidade = valores[1];

        tvTemperatura.setText(temperatura.concat(" °C"));
        tvUmidade.setText(umidade.concat(" %"));
    }
}
