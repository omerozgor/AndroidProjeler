package com.ozgortech.landmarkbookwithrw;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.ozgortech.landmarkbookwithrw.databinding.RecyclerRowBinding;

import java.util.ArrayList;

public class LandmarkAdapter extends RecyclerView.Adapter<LandmarkAdapter.LandmarkHolder> {

    //bu miras aldığımız sınıf kendi metodlarında kulanmak için bizden generic olarak bir ViewHolder
    //istiyor. Biz de bu sınıfın içinde ViewHolderı extend eden bir alt sınıf oluşturduk.
    //İsmi LandmarkHolder

    ArrayList<Landmark> landmarkArrayList;

    public LandmarkAdapter(ArrayList<Landmark> landmarkArrayList) {
        this.landmarkArrayList = landmarkArrayList;
    }

    @NonNull
    @Override
    public LandmarkHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        //burada bindingimizi bağlama işlemini yapacağız...
        RecyclerRowBinding binding = RecyclerRowBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new LandmarkHolder(binding);
        //bu metod bizden ViewHolder (yani landmark holder döndürmemizi istiyor). Bİz de
        //bindingimizi bağlayıp döndürdüğümüz landmarkholdera parametre olarak verdik

    }

    @Override
    public void onBindViewHolder(@NonNull LandmarkHolder holder, @SuppressLint("RecyclerView") int position) {
        //Layoutumuzun içinde hangi verileri göstermek istiyorsak burada göstereceğiz
        holder.binding.textView3.setText(landmarkArrayList.get(position).getName());
        //burası son aşama. artık bindingimiz hazır ve oluşturduğumuz layouta ulaşabiliyoruz
        //oradaki texte ne yazacağımızı belirtiyoruz
        //tıklanma işleminde ne olacağını da burada yapıyoruz
        holder.itemView.setOnClickListener(new View.OnClickListener() {
            //binding ile layoutumuzdaki her bir ögeye(text gibi, ileride resim falan da
            // olabilir) ulaşabiliyorken holder.itemView diğerek direkt tıklanan itemi yani
            // layotun hepsini temsil etmiş oluyoruz.Somuçta tıklanma işlemi text üzeinden değil de
            //item üzerinden yürüyen bir işlem.

            @Override
            public void onClick(View view) {
                Intent intent = new Intent(holder.itemView.getContext(),DetailsActivity.class);
                //burası bir activity olmadığı için contexti itemviewin olduğu yerin contexti olacak
                //şekilde ayarlıyoruz
                //intent.putExtra("landmark",landmarkArrayList.get(position));
                //normalde intent ile yollardık ama bu sefer farklı bir yöntem
                // görelim dedik(singleton)
                Singleton singleton = Singleton.getInstance();//static singleton nesnemizi oluşturduk
                singleton.setLandmark(landmarkArrayList.get(position));//bu metod ile
                //nesnemizin landmarkını set ettik
                holder.itemView.getContext().startActivity(intent);//burası yine bir aktivite
                //olmadığı için startActivity direkt hazır gelmiyor bize. Context üzerinden
                // erişiyoruz
            }
        });
    }

    @Override
    public int getItemCount() {
        //Bizim oluşturacağımız xml'imizin yani layoutumuzun kaç kere
        //oluşturulacağını dönüyor bu metod. Recycler view'in listviewden temel farkı
        //aynı olan itemleri tekrar tekrar oluşturmayıp bir nevi geri dönüştürüp
        //kullanması. Listview de biz aynı itemden 1000 kere de koysak 1000 kere onu baştan
        //oluşturuyor. Şu an bizim listemizde 4 farklı item oldupundan burayı 4 dönecek şekilde
        //ayarlayacağız
        //4 bizim arraylistimizin eleman sayısı. Peki ona buradan nasıl ulaşacağız?
        //Globale bir arraylist tanımlayıp onu constructordan alacağız
        return landmarkArrayList.size();
    }

    public class LandmarkHolder extends RecyclerView.ViewHolder{
        //Bu landmarkHolderımız her oluşturulduğunda constructerında bir adet binding alsın dedik
        private RecyclerRowBinding binding;
        public LandmarkHolder(RecyclerRowBinding binding) {
            super(binding.getRoot());
            this.binding = binding;

        }
    }
}
