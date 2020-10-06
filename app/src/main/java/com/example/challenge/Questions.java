package com.example.challenge;

public class Questions {
    public String mQuestions[]=
            {
                    "Czy jestes osoba aktywna fizycznie?",
                    "Ile razy w tygodniu uprawia Pan/Pani sport?",
                    "Czy jestes osoba która przebiegnie 5km w tempie 4min na km",
                    "Czy uważasz, że odżywiasz się zdrowo",
                    "Ile przeciętnie trwa Pana/Pani wysiłek fizyczny?",
                    "Ktora aktywnosc lubisz najbardziej",
            };
    private String mChoices [][]=
            {
                    {"bardzo", "srednio", "slabo", "kompletnie sie nie ruszam"},
                    {"Częściej niż 4 razy w tygodniu", "1-2 razy w tygodniu", "1-3 razy w miesiącu", "nie uprawiam sportu"},
                    {"w łatwy sposob", "raczej tak", "z problemami ale tak", "ciezko"},
                    {"tak", "czasami", "raczej nie", "kompletnie nie"},
                    {"powyzej 90 minut", " 30 - 80 minut","Poniżej 15 minut", "0-3 minuty"},
                    {"bieganie", "rower", "rolki", "spacer"},

            };
    private String mCorrectAnswersforpro[]={"bardzo","Częściej niż 4 razy w tygodniu","w łatwy sposob","tak","powyzej 90 minut","bieganie"};
    private String mCorrectAnswersformedium[]={"srednio","1-2 razy w tygodniu","raczej tak","czasami","30 - 80 minut","rower"};
    private String mCorrectAnswersforlow[]={"slabo","1-3 razy w miesiącu","z problemami ale tak","raczej nie","Poniżej 15 minut","rolki"};
    private String mCorrectAnswersforverylow[]={"kompletnie sie nie ruszam","nie uprawiam sportu","ciezko","kompletnie nie","0-3 minuty","spacer"};
    public String getQuestion(int a){
        String question= mQuestions[a];
        return  question;
    }
    public String getChoice1(int a){
        String choice= mChoices[a][0];
        return  choice;
    }
    public String getChoice2(int a){
        String choice= mChoices[a][1];
        return  choice;
    }
    public String getChoice3(int a){
        String choice= mChoices[a][2];
        return  choice;
    }
    public String getChoice4(int a){
        String choice= mChoices[a][3];
        return  choice;
    }
    public String getCorrectAnswer(int a)
    {
        String answer= mCorrectAnswersforpro[a];
        return answer;
    }
    public String getCorrectAnswersformedium(int a)
    {
        String answer= mCorrectAnswersforpro[a];
        return answer;
    }
    public String getCorrectAnswersforlow(int a)
    {
        String answer= mCorrectAnswersforpro[a];
        return answer;
    }
    public String getCorrectAnswersforverylow(int a)
    {
        String answer= mCorrectAnswersforpro[a];
        return answer;
    }

}
