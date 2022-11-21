package com.ozgortech.landmarkbookwithrw;

public class Singleton {
    //Singleton tek bir nesneye sahip sınıf demektir. Constructoru private yaparak elde edilir.
    //Normalde static kullanarak da aynı objeye farklı yerlerden ulaşabiliriz ama bilirsiniz pek
    //güvensiz bir yol. Bunun da temel mantığı aynı ama cilalanmış hali diyebiliriz.

    private Landmark landmark;
    private static  Singleton singleton;
    private Singleton() {
    }

    public Landmark getLandmark() {
        return landmark;
    }

    public void setLandmark(Landmark landmark) {
        this.landmark = landmark;

    }

    public static Singleton getInstance(){ //constructorımız static olduğu için bu sınıfı
        //bu sınıf dışında newlemek mümkün değil. O nedenle getInstance isimli bir metod oluşturup
        //burada instancesını oluşturuyoruz. Bu sınıf metod kullanıldığında static bir singleton
        //nesnemzz oluşacak veya varsa hep aynı nesnemiz döenecek
        if (singleton == null){
            singleton = new Singleton();//eğer nesnemiz daha önceden oluşturulmamış ise oluştur
            //oluşturulmuşsa direkt onu dönç
        }
        return singleton;
    }
}
