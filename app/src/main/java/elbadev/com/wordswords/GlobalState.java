package elbadev.com.wordswords;

import android.app.Application;
import android.arch.persistence.room.Room;
import android.content.Intent;
import android.media.MediaPlayer;
import android.text.Layout;
import android.view.LayoutInflater;
import android.widget.Toast;

import com.github.nkzawa.socketio.client.Socket;

import org.json.JSONObject;

import java.util.Random;

/**
 * Created by frome on 26/07/2017.
 */

public class GlobalState extends Application {

    static private Socket mSocket;
    private static Boolean setup_payment_done = false;
    private static String address = "http://46.101.105.29/" ;//SERVER REMOTO
   //private static String address = "http://192.168.1.35:3000/";//SERVER UFFICIO
//   private static final String base64EncodePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApCM/a4Nn5xBE3fFeSrOv3QqiA7IoCfy08x9BRP7PrmP9PgDihCLAPjR+5BeUy7cIl4kIayUVAd0qZcccDdgSMkgkx5jBexGDyrDpUkaybGVvWIDZ3SKidp9JxFwDE5zyFn52NNy3Sp/+k/walE2c+3YV5QA8p1OF/2qV71cYuCploU5vX1LXYmu6NEG77lccZ2gQKC3VwrBUCKuZnoSUBdfqtn1gzhJmb7h1f1LKdNwz7vwJtux5N0IRouPHkFwuX7CRbmZBhocRbLR5kge5NUhibY4FBNCFUUSN967qmo+v69fZcrY6cSpAIdtuOJKHDcULZCJMf/62ARwiN/sFHQIDAQAB";
    private static final String base64EncodePublicKey = "MIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEApCM/a4Nn5xBE3fFeSrOv3QqiA7IoCfy08x9BRP7PrmP9PgDihCLAPjR+5BeUy7cIl4kIayUVAd0qZcccDdgSMkgkx5jBexGDyrDpUkaybGVvWIDZ3SKidp9JxFwDE5zyFn52NNy3Sp/+k/walE2c+3YV5QA8p1OF/2qV71cYuCploU5vX1LXYmu6NEG77lccZ2gQKC3VwrBUCKuZnoSUBdfqtn1gzhJmb7h1f1LKdNwz7vwJtux5N0IRouPHkFwuX7CRbmZBhocRbLR5kge5NUhibY4FBNCFUUSN967qmo+v69fZcrY6cSpAIdtuOJKHDcULZCJMf/62ARwiN/sFHQIDAQAB";
    static private Integer richiesteVolleyInCorso = 0;
    static private String frase_giusta_inizio = "";
    static private String frase_giusta_fine = "";
    static private MediaPlayer typewriter_button_sound_0;
    static private MediaPlayer typewriter_button_sound_1;
    static private MediaPlayer typewriter_button_sound_2;
    static private MediaPlayer typewriter_button_sound_3;
    static private MediaPlayer typewriter_button_sound_4;
    static private MediaPlayer typewriter_multi_sound;
    static private MediaPlayer typewriter_drin;
    static private MediaPlayer typewriter_paper;
    static private MediaPlayer typewriter_paper_3;
    static private String mia_email;
    static private String mia_password;
    static private Integer leader = 0;
    static private String id_stanza_attuale;
    static private Integer posizione_mia_frase;
    static private Integer posizione_lista;
    static private Integer turno;
    static private JSONObject classifica;
    static private String date_alive = "";
    static private Intent turno_gioco = null;
    static private Intent turno_classifica;
    static private Intent desktop;
    static private Integer ultimo_turno;
    static private Boolean rebootta = false;
    static private Boolean prima_esistenza = true;
    static private Boolean abbasta = false;
    static private Boolean sofia = false;
    static private Integer miei_punti = 0;
    static private String utenti_online[] = null ;
    static private String vincitore;
    static private Integer punti_vincitore;
    static private AppDatabase db;


    public static final String getBase64publicKey(){ return  GlobalState.base64EncodePublicKey; }
    public static Socket getmSocket() {
        return mSocket;
    }

    public static void setmSocket(Socket mSocket) {
        GlobalState.mSocket = mSocket;
    }

    public static String getAddress() {
        return address;
    }

    public static String getMia_email() {
        return mia_email;
    }

    public static void setMia_email(String mia_email) {
        GlobalState.mia_email = mia_email;
    }

    public static String getId_stanza_attuale() {
        return id_stanza_attuale;
    }

    public static void setId_stanza_attuale(String id_stanza_attuale) {
        GlobalState.id_stanza_attuale = id_stanza_attuale;
    }

    public static Integer getLeader() {
        return leader;
    }

    public static void setLeader(Integer leader) {
        GlobalState.leader = leader;
    }

    public static Integer getPosizione_lista() {
        return posizione_lista;
    }

    public static void setPosizione_lista(Integer posizione_lista) {
        GlobalState.posizione_lista = posizione_lista;
    }

    public static JSONObject getClassifica() {
        return classifica;
    }

    public static void setClassifica(JSONObject classifica) {
        GlobalState.classifica = classifica;
    }

    public static Integer getTurno() {
        return turno;
    }

    public static void setTurno(Integer turno) {
        GlobalState.turno = turno;
    }

    public static String getDate_alive() {
        return date_alive;
    }

    public static void setDate_alive(String date_alive) {
        GlobalState.date_alive = date_alive;
    }

    public static Intent getTurno_gioco() {
        return turno_gioco;
    }

    public static void setTurno_gioco(Intent turno_gioco) {
        GlobalState.turno_gioco = turno_gioco;
    }

    public static Intent getTurno_classifica() {
        return turno_classifica;
    }

    public static void setTurno_classifica(Intent turno_classifica) {
        GlobalState.turno_classifica = turno_classifica;
    }

    public static Integer getUltimo_turno() {
        return ultimo_turno;
    }

    public static void setUltimo_turno(Integer ultimo_turno) {
        GlobalState.ultimo_turno = ultimo_turno;
    }

    public static Integer getPosizione_mia_frase() {
        return posizione_mia_frase;
    }

    public static void setPosizione_mia_frase(Integer posizione_mia_frase) {
        GlobalState.posizione_mia_frase = posizione_mia_frase;
    }

    public static String getVincitore() {
        return vincitore;
    }

    public static void setVincitore(String vincitore) {
        GlobalState.vincitore = vincitore;
    }

    public static Integer getPunti_vincitore() {
        return punti_vincitore;
    }

    public static void setPunti_vincitore(Integer punti_vincitore) {
        GlobalState.punti_vincitore = punti_vincitore;
    }

    public static Boolean getRebootta() {
        return rebootta;
    }

    public static void setRebootta(Boolean rebootta) {
        GlobalState.rebootta = rebootta;
    }

    public static Boolean getPrima_esistenza() {
        return prima_esistenza;
    }

    public static void setPrima_esistenza(Boolean prima_esistenza) {
        GlobalState.prima_esistenza = prima_esistenza;
    }

    public static Integer getRichiesteVolleyInCorso() {
        return richiesteVolleyInCorso;
    }

    public static void setRichiesteVolleyInCorso(Integer richiesteVolleyInCorso) {
        GlobalState.richiesteVolleyInCorso = richiesteVolleyInCorso;
    }

    public static String getMia_password() {
        return mia_password;
    }

    public static void setMia_password(String mia_password) {
        GlobalState.mia_password = mia_password;
    }

    public static MediaPlayer getTypewriter_button_sound_0() {
        return typewriter_button_sound_0;
    }

    public static void setTypewriter_button_sound_0(MediaPlayer typewriter_button_sound_0) {
        GlobalState.typewriter_button_sound_0 = typewriter_button_sound_0;
    }

    public static MediaPlayer getTypewriter_button_sound_1() {
        return typewriter_button_sound_1;
    }

    public static void setTypewriter_button_sound_1(MediaPlayer typewriter_button_sound_1) {
        GlobalState.typewriter_button_sound_1 = typewriter_button_sound_1;
    }

    public static MediaPlayer getTypewriter_button_sound_2() {
        return typewriter_button_sound_2;
    }

    public static void setTypewriter_button_sound_2(MediaPlayer typewriter_button_sound_2) {
        GlobalState.typewriter_button_sound_2 = typewriter_button_sound_2;
    }

    public static MediaPlayer getTypewriter_button_sound_3() {
        return typewriter_button_sound_3;
    }

    public static void setTypewriter_button_sound_3(MediaPlayer typewriter_button_sound_3) {
        GlobalState.typewriter_button_sound_3 = typewriter_button_sound_3;
    }

    public static MediaPlayer getTypewriter_button_sound_4() {
        return typewriter_button_sound_4;
    }

    public static void setTypewriter_button_sound_4(MediaPlayer typewriter_button_sound_4) {
        GlobalState.typewriter_button_sound_4 = typewriter_button_sound_4;
    }

    public static MediaPlayer getTypewriter_multi_sound() {
        return typewriter_multi_sound;
    }

    public static void setTypewriter_multi_sound(MediaPlayer typewriter_multi_sound) {
        GlobalState.typewriter_multi_sound = typewriter_multi_sound;
    }

    public static Intent getDesktop() {
        return desktop;
    }

    public static void setDesktop(Intent desktop) {
        GlobalState.desktop = desktop;
    }

    public static Boolean getAbbasta() {
        return abbasta;
    }

    public static void setAbbasta(Boolean abbasta) {
        GlobalState.abbasta = abbasta;
    }

    public static MediaPlayer getTypewriter_drin() {
        return typewriter_drin;
    }

    public static void setTypewriter_drin(MediaPlayer typewriter_drin) {
        GlobalState.typewriter_drin = typewriter_drin;
    }

    public static MediaPlayer getTypewriter_paper() {
        return typewriter_paper;
    }

    public static void setTypewriter_paper(MediaPlayer typewriter_paper) {
        GlobalState.typewriter_paper = typewriter_paper;
    }

    public static MediaPlayer getTypewriter_paper_3() {
        return typewriter_paper_3;
    }

    public static void setTypewriter_paper_3(MediaPlayer typewriter_paper_3) {
        GlobalState.typewriter_paper_3 = typewriter_paper_3;
    }

    public static Boolean getSofia() {
        return sofia;
    }

    public static void setSofia(Boolean sofia) {
        GlobalState.sofia = sofia;
    }

    public static Integer getMiei_punti() {
        return miei_punti;
    }

    public static void setMiei_punti(Integer miei_punti) {
        GlobalState.miei_punti = miei_punti;
    }

    public static String[] getUtenti_online() {
        return utenti_online;
    }

    public static void setUtenti_online(String[] utenti_online) {
        GlobalState.utenti_online = utenti_online;
    }

    public static Boolean getSetup_payment_done() {
        return setup_payment_done;
    }

    public static void setSetup_payment_done(Boolean setup_payment_done) {
        GlobalState.setup_payment_done = setup_payment_done;
    }

    public static AppDatabase getDb() {
        return db;
    }

    public static void setDb(AppDatabase db) {
        GlobalState.db = db;
    }

    public static String getFrase_giusta_inizio() {
        return frase_giusta_inizio;
    }

    public static void setFrase_giusta_inizio(String frase_giusta_inizio) {
        GlobalState.frase_giusta_inizio = frase_giusta_inizio;
    }

    public static String getFrase_giusta_fine() {
        return frase_giusta_fine;
    }

    public static void setFrase_giusta_fine(String frase_giusta_fine) {
        GlobalState.frase_giusta_fine = frase_giusta_fine;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        // Required initialization logic here!
    }
    public static MediaPlayer getButtonSound(){
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(4);
        switch (randomInt){
            case 0:
                return getTypewriter_button_sound_0();
            case 1:
                return getTypewriter_button_sound_1();
            case 2:
                return getTypewriter_button_sound_2();
            case 3:
                return getTypewriter_button_sound_3();
            case 4:
                return getTypewriter_button_sound_4();
            default:
                return getTypewriter_button_sound_0();
        }

    }
    public static MediaPlayer getTypingSound(){
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(2);
        switch (randomInt){
            case 0:
                return getTypewriter_button_sound_0();
            case 1:
                return getTypewriter_button_sound_1();
            case 2:
                return getTypewriter_button_sound_2();
            default:
                return getTypewriter_button_sound_0();
        }

    }
    public static String getRandomWisenessBackButton(){
        Random randomGenerator = new Random();
        int randomInt = randomGenerator.nextInt(9);
        switch (randomInt){
            case 0:
                return "Non ci torni indietro.";
            case 1:
                return "Sempre andare avanti nella vita.";
            case 2:
                return "Ricordati i punti in fondo alle frasi.";
            case 3:
                return "Se mio nonno aveva 3 palle era un flipper.";
            case 4:
                return "Ricordati sempre che sei una persona unica. Proprio come tutti gli altri.";
            case 5:
                return "Hai proprio un bel faccino!";
            case 6:
                return "Mi dispiace ma se tu avessi ragione, sarei daccordo con te.";
            case 7:
                return "Posso resistere a tutto eccetto che alle tentazioni.";
            case 8:
                return "Siamo tutti nati pazzi. Qualcuno è rimasto così.";
            case 9:
                return "Non sarai mai veramente forte finchè non trovi il lato divertente alle cose.";
            default:
                return "Un giorno senza alba è come, hai presente... la notte?";
        }
    }


}
