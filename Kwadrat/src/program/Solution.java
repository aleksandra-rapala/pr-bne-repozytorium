

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Scanner;


public class Solution {

    public static void main(String[] args) {

        Command object = null;                                                  //ŻĄDANIE
        Invoker invoker;                                                        //NADAWCA
        RepozytoriumKwadratow listaKwadratow = new RepozytoriumKwadratow();     //ODBIORCA
        CommandExecutor commandExecutor = new CommandExecutor();                //WYKONAWCA

        String[] komendaArray;
        do  {

            Scanner scan = new Scanner(System.in);
            //System.out.println("Podaj komendę:");
            komendaArray = scan.nextLine().split(" ");


            // Klient tworzy konkretne obikety żądań
            //Klient przekazuje wszystkie parametry żądania, włącznie z instancją odbiorcy, do konstruktora polecenia
            switch (komendaArray[0]) {
                case "C" -> object = new Create(Integer.parseInt(komendaArray[1]), Integer.parseInt(komendaArray[2]), listaKwadratow);
                case "M" -> object = new Move(Integer.parseInt(komendaArray[1]), Integer.parseInt(komendaArray[2]), Integer.parseInt(komendaArray[3]), listaKwadratow);
                case "S" -> object = new Scale(Integer.parseInt(komendaArray[1]), Integer.parseInt(komendaArray[2]), listaKwadratow);
                case "U" -> object = new Undo();
                case "R" -> object = new Redo();
                case "P" -> object = new Print(listaKwadratow);
            }


            //Klient następnie otrzymane polecenie kojarzy w tym przypadku z jednym nadawcą
            invoker = new Invoker(object, commandExecutor);
            invoker.run();
        }while(!komendaArray[0].equals("999"));
    }













    static class Undo implements Command {

        @Override
        public void executeCommand(HistoryCommand lista){

            //odwracamy listę by mieć na pierwszym miejscu ostatnio wykonane akcje
            Collections.reverse(lista.getLista());

            for(Command komenda : lista.getLista()){
                if (komenda.getClass().equals(Create.class) || (komenda.getClass().equals(Move.class)) || (komenda.getClass().equals(Scale.class))){
                    if(!komenda.getAnulowany()){                //jesli komenda nie jest anulowana to można ją zanulowac
                        komenda.setAnulowany(true);             //ustaw wiadomosc ze anulowana akcja
                        komenda.cofnijCommand();                //cofnij komende
                        Collections.reverse(lista.getLista());
                        return;
                    }
                }
            }
            Collections.reverse(lista.getLista());
        }

        @Override
        public String toString () {
            return "U";
        }
    }





static class Scale implements Command {

        //atrybuty
        private final int i;
        private final int j;
        private final RepozytoriumKwadratow lista;
        private boolean anulowany;

        //konstruktor
    Scale(int i, int j, RepozytoriumKwadratow lista){
            this.i = i;
            this.j = j;
            this.lista = lista;
            this.anulowany = false;
        }

        //gettry
        public boolean getAnulowany(){
            return anulowany;
        }

        //settery
           public void setAnulowany(boolean value){
            this.anulowany=value;
        }


        @Override
        public void executeCommand(){

            for(Kwadrat kwadrat : lista.getLista()){
                if(kwadrat.getNumerId()==i){
                    kwadrat.setBokMnozeniem(j);
                }
            }
        }

        @Override
        public void cofnijCommand(){

            for(Kwadrat kwadrat : lista.getLista()){
                if(kwadrat.getNumerId()==i){

                    kwadrat.setBokDzieleniem(j);
                }
            }
        }

        @Override
        public String toString () {
            return "S";
        }
    }






    static class RepozytoriumKwadratow {

        private final List<Kwadrat> listaKwadratow;

        public RepozytoriumKwadratow(){ this.listaKwadratow = new ArrayList<>(); }
        public void dodajKwadrat(Kwadrat object){ listaKwadratow.add(object); }
        public void usunKwadrat(Kwadrat object){
            listaKwadratow.remove(object);
        }
        public List<Kwadrat> getLista(){
            return listaKwadratow;
        }
    }






    static class Redo implements Command {

        @Override
        public void executeCommand(HistoryCommand lista) {

            HistoryCommand listaCopy = new HistoryCommand();
            listaCopy.kopiujListe(lista);
            Collections.reverse(lista.getLista());


            for (Command komenda : lista.getLista()) {

                if(komenda.getClass().equals(Print.class)){
                    continue;
                }

                if(komenda.getClass().equals(Undo.class) || (komenda.getClass().equals(Redo.class))) {

                    for (Command komenda2 : listaCopy.getLista()) {
                        if (komenda2.getAnulowany()) {
                            komenda2.setAnulowany(false);
                            komenda2.executeCommand();
                            return;
                        }
                    }
                }

                Collections.reverse(lista.getLista());
                return;
            }
            Collections.reverse(lista.getLista());  //gry brak elementow komend
        }


        @Override
        public String toString () {
            return "R";
        }
    }






    static class Print implements Command {

        //atrybuty
        private final RepozytoriumKwadratow lista;

        //konstruktor
        public Print(RepozytoriumKwadratow lista){
            this.lista = lista;
        }


        @Override
        public void executeCommand(){

            Collections.sort(lista.getLista());
            for(Kwadrat kwadrat : lista.getLista()){
                System.out.println(kwadrat.getNumerId() +" "+ kwadrat.getX() + " " + kwadrat.getY() + " " + kwadrat.getBok());
            }
        }


        @Override
        public String toString () {
            return "P";
        }

    }








    static class Move implements Command {

        //atrybuty
        private final int i;
        private final int j;
        private final int k;
        private final RepozytoriumKwadratow lista;
        private boolean anulowany;

        //konstruktor
        public Move(int i, int j, int k, RepozytoriumKwadratow lista){
            this.i = i;
            this.j = j;
            this.k = k;
            this.lista = lista;
            this.anulowany = false;
        }


        //settery
        public void setAnulowany(boolean value){
            this.anulowany=value;
        }

        //gettery
        public boolean getAnulowany(){
            return anulowany;
        }



        @Override
        public void executeCommand(){

            for(Kwadrat kwadrat : lista.getLista()){
                if(kwadrat.getNumerId()==i){
                    kwadrat.setX(j);
                    kwadrat.setY(k);
                }
            }
        }


        @Override
        public void cofnijCommand(){

            for(Kwadrat kwadrat : lista.getLista()){
                if(kwadrat.getNumerId()==i){
                    kwadrat.setX(-j);
                    kwadrat.setY(-k);
                }
            }
        }


        @Override
        public String toString () {
            return "M";
        }
    }











    static class Kwadrat implements Comparable<Kwadrat> {

        //atrybuty
        int numer_id;
        int dlugosc_boku;
        int x;
        int y;

        //konstruktor
        public Kwadrat(int i, int j){
            numer_id=i;
            dlugosc_boku=j;
            x=0;
            y=0;
        }

        //gettery
        public int getNumerId(){
            return numer_id;
        }
        public int getX() {
            return x;
        }
        public int getY(){
            return y;
        }
        public int getBok() {
            return dlugosc_boku;
        }

        //settery
        public void setX(int j) {
            x+=j;
        }
        public void setY(int k) {
            y+=k;
        }
        public void setBokMnozeniem(int j) {
            dlugosc_boku*=j;
        }
        public void setBokDzieleniem(int j) {
            dlugosc_boku/=j;
        }

        //nadpisane
        @Override
        public int compareTo(Kwadrat o) {
            return Integer.compare(this.getNumerId(), o.getNumerId());
        }

        @Override
        public String toString (){
            return this.getNumerId() + " " + this.getX() + " " + this.getY() + " " + this.getBok();
        }
    }







    static class Invoker {

       //Nadawca nie jest odpowiedzialny za tworzenie obiektu polecenie
       private final Command object;                                         //ŻĄDANIE  -> zawiera pole przechowujące odniesienie do obiektu polecenia
       private final CommandExecutor commandExecutor;                        //WYKONAWCA


        //Otrzymuje wcześniej przygotowane polecenie od klienta za pośrednictwem konstruktora
        public Invoker(Command object, CommandExecutor commandExecutor){
           this.object = object;                           //INICJUJE ŻĄDANIE
           this.commandExecutor = commandExecutor;
        }


        public void run() {
            commandExecutor.wykonaj(object);
        }

    }






    static class HistoryCommand {

        private final List<Command> listaKomend;

        public HistoryCommand(){ this.listaKomend = new ArrayList<>(); }
        public List<Command> getLista(){
            return this.listaKomend;
        }
        public void dodajCommand(Command object){
            this.listaKomend.add(object);
        }

        public void kopiujListe(HistoryCommand lista){
            this.listaKomend.addAll(lista.getLista());
        }
    }






    static class Create implements Command {

        //atrybuty
        private final int i;
        private final int j;
        private final RepozytoriumKwadratow lista;
        private boolean anulowany;

        private boolean nadpisanie = false;
        private Kwadrat nadpisanyKwadrat;


        //konstruktor
        public Create(int i, int j, RepozytoriumKwadratow lista) {
            this.i = i;
            this.j = j;
            this.lista = lista;
            this.anulowany = false;
        }




        @Override
        public void executeCommand(){

        for(Kwadrat kwadrat : lista.getLista()){                       //iterujemy po liście
            if(kwadrat.getNumerId()==i){                    //jeśli znajdzie istniejący juz kwadrat
                nadpisanie=true;
                nadpisanyKwadrat=kwadrat;                   //najpierw zapamięta kwadrat nadpisany w razie cofnięcia akcji
                lista.usunKwadrat(kwadrat);                      //po czym usunie z listy właściwych kwadrató kwadratów
                break;
            }
        }
            Kwadrat nowyKwadrat = new Kwadrat(i, j);        //tworzymy nowy obiekt kwadratu
            lista.dodajKwadrat(nowyKwadrat);                         //i wkladamy do listy
        }


        @Override
        public void cofnijCommand() {

            for (Kwadrat kwadrat : lista.getLista()) {                 //iterujemy po liście
                if (kwadrat.getNumerId() == i) {            //znajdujemy nasz kwadrat
                    lista.usunKwadrat(kwadrat);                  //po czym go usuwamy
                    break;
                }
            }

            //ALE MUSIMY SPRAWZDIĆ CZY TEN KWADRAT NIE NADPISYWAŁ INNEGO
            if (nadpisanie) {
                nadpisanie=false;
                lista.dodajKwadrat(nadpisanyKwadrat);
            }
        }


            @Override
            public String toString () {
                return "C";
            }




        public boolean getAnulowany(){
            return anulowany;
        }
        public void setAnulowany(boolean value){
            this.anulowany=value;
        }

    }





    static class CommandExecutor {

        private final HistoryCommand listaKomend;


        public CommandExecutor(){
            this.listaKomend = new HistoryCommand();
        }


            public void wykonaj(Command object){

                if (object.getClass().equals(Create.class) || (object.getClass().equals(Move.class)) || (object.getClass().equals(Scale.class)) || (object.getClass().equals(Print.class))){
                    object.executeCommand();
                }
                else{
                    object.executeCommand(listaKomend);
                }

                listaKomend.dodajCommand(object); //lista z komendami wraz z atrybutami


        }

    }







    interface Command {

        default void executeCommand(){ }
        default void executeCommand(HistoryCommand listaKomend){ }


        default void cofnijCommand(){ }
        default boolean getAnulowany(){ return false;}
        default void setAnulowany(boolean b){}

    }
}