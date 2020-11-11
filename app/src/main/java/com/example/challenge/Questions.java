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
                    "Jaką rolę odgrywa aktywność ruchowa w twoim życiu?",
                    "Jak oceniasz swój stan zdrowia?",
                    "Czy uważasz że Twoja aktywność fizyczna jest wystarczająca?",
                    "Jak ocenia Pani/Pan swoją masę ciała?"
            };
    private String mChoices [][]=
            {
                    {"bardzo", "średnio", "słabo", "kompletnie sie nie ruszam"},
                    {"Częściej niż 4 razy w tygodniu", "1-2 razy w tygodniu", "1-3 razy w miesiącu", "nie uprawiam sportu"},
                    {"w łatwy sposob", "raczej tak", "z problemami ale tak", "ciezko"},
                    {"tak", "czasami", "raczej nie", "kompletnie nie"},
                    {"powyzej 90 minut", "30 - 80 minut","Poniżej 15 minut", "0-3 minuty"},
                    {"bieganie", "rower", "marszobieg", "spacer"},
                    {"bardzo duzą rolę", "czasem lubię się porusząć", "mała rola", "żadna rola"},
                    {"bardzo dobrze", "dobrze", "ani dobrze ani źle ", "źle"},
                    {"jeszcze nie", "tak", "raczej nie ", "zdecydowanie nie"},
                    {"prawidłowa", "niemal prawidłowa ", "za duzo waże ", "jestem otyły/otyła"},

            };
    private String mCorrectAnswersforpro[]={"bardzo","Częściej niż 4 razy w tygodniu","w łatwy sposob","tak","powyzej 90 minut",
            "bieganie","bardzo duzą rolę","bardzo dobrze","jeszcze nie","prawidłowa"};
    private String mCorrectAnswersformedium[]={"średnio","1-2 razy w tygodniu","raczej tak","czasami","30 - 80 minut",
            "rower","czasem lubię się porusząć","dobrze","tak","niemal prawidłowa "};
    private String mCorrectAnswersforlow[]={"słabo","1-3 razy w miesiącu","z problemami ale tak","raczej nie","Poniżej 15 minut",
            "marszobieg","mała rola","ani dobrze ani źle ","raczej nie ","za duzo waże "};
    private String mCorrectAnswersforverylow[]={"kompletnie sie nie ruszam","nie uprawiam sportu","ciezko","kompletnie nie","0-3 minuty",
            "spacer","żadna rola","źle","zdecydowanie nie","jestem otyły/otyła"};
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
        String answer= mCorrectAnswersformedium[a];
        return answer;
    }
    public String getCorrectAnswersforlow(int a)
    {
        String answer= mCorrectAnswersforlow[a];
        return answer;
    }
    public String getCorrectAnswersforverylow(int a)
    {
        String answer= mCorrectAnswersforverylow[a];
        return answer;
    }

}
