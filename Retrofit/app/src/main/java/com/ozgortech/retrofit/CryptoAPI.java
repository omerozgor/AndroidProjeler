package com.ozgortech.retrofit;

import java.util.List;

import io.reactivex.rxjava3.core.Observable;
import retrofit2.Call;
import retrofit2.http.GET;

public interface CryptoAPI {

    // Apimiz için arayüz yazıyoruz... genelde apiler ile aşağıdaki işlemler yapılır biz sadece
    // verileri çekeceğimiz için get işlemini yapacağız.

    // GET, POST, UPDATE, DELETE

    // https://raw.githubusercontent.com/
    // atilsamancioglu/K21-JSONDataSet/master/crypto.json
    //https://api.nomics.com/v1/   --> BASE
    // currencies/ticker?key=60e92823fba866d048a5144933ff36927d6921d5 --> EK

    @GET("currencies/ticker?key=60e92823fba866d048a5144933ff36927d6921d5")
    Observable<List<CryptoModel>> getData();
    // Call arayüzü hani RxJava'nın Flowable'ı tarzı bişey server'a istek yollayıp serverdan
    // cevap bekliyor. Asenkron veya senkron bir şekilde çalıştırılabilir. Asenkron çalıştırmak
    // tabiki daha mantıklı. Asenkron çalıştırmak için mainActivity'de enqueue kulanacağız.
    // Üstteki retrofitin get Anotasyonunu ile de bir get işlemi yapacağımızı ve apimizi veriyoruz
    // ama bu method sadece api ile ilgili olduğundan sitenin base kısmını değil ek kısmını veriyoruz
    // base kısmını ise retrofitin kendisini mainActivity de oluştururken vereceğiz

}
