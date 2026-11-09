package org.insbaixcamp.llistacompra.utils

fun obtenirCategoria(nomProducte: String): String {

    val nom = nomProducte.lowercase().trim()

    return when {

        // LÀCTICS
        nom.contains("llet") ||
                nom.contains("formatge") ||
                nom.contains("iogurt") ||
                nom.contains("mantega") ||
                nom.contains("nata") ||
                nom.contains("kefir") ||
                nom.contains("mozzarella") ||
                nom.contains("parmesà") ||
                nom.contains("queso") ->
            "Làctics"


        // FRUITA I VERDURA
        nom.contains("poma") ||
                nom.contains("pomes") ||
                nom.contains("plàtan") ||
                nom.contains("plàtans") ||
                nom.contains("banana") ||
                nom.contains("taronja") ||
                nom.contains("mandarina") ||
                nom.contains("pera") ||
                nom.contains("préssec") ||
                nom.contains("nectarina") ||
                nom.contains("kiwi") ||
                nom.contains("raïm") ||
                nom.contains("maduixa") ||
                nom.contains("maduixes") ||
                nom.contains("síndria") ||
                nom.contains("meló") ||
                nom.contains("pinya") ||
                nom.contains("mango") ||
                nom.contains("llimona") ||
                nom.contains("alvocat") ||
                nom.contains("tomàquet") ||
                nom.contains("tomàquets") ||
                nom.contains("enciam") ||
                nom.contains("pastanaga") ||
                nom.contains("carbassó") ||
                nom.contains("carbassa") ||
                nom.contains("albergínia") ||
                nom.contains("bròquil") ||
                nom.contains("coliflor") ||
                nom.contains("espinacs") ||
                nom.contains("patata") ||
                nom.contains("patates") ||
                nom.contains("ceba") ||
                nom.contains("all") ||
                nom.contains("pebrot") ||
                nom.contains("cogombre") ->
            "Fruita i verdura"


        // CARN I PEIX
        nom.contains("pollastre") ||
                nom.contains("vedella") ||
                nom.contains("porc") ||
                nom.contains("xai") ||
                nom.contains("conill") ||
                nom.contains("hamburguesa") ||
                nom.contains("hamburgueses") ||
                nom.contains("botifarra") ||
                nom.contains("salsitxa") ||
                nom.contains("pernil") ||
                nom.contains("pit de pollastre") ||
                nom.contains("peix") ||
                nom.contains("salmó") ||
                nom.contains("tonyina") ||
                nom.contains("bacallà") ||
                nom.contains("lluç") ||
                nom.contains("dorada") ||
                nom.contains("orada") ||
                nom.contains("gambes") ||
                nom.contains("gamba") ||
                nom.contains("llagostins") ||
                nom.contains("calamars") ||
                nom.contains("musclos") ->
            "Carn i peix"


        // PA I CEREALS
        nom == "pa" ||
                nom.contains("baguette") ||
                nom.contains("barra de pa") ||
                nom.contains("pa de motlle") ||
                nom.contains("arròs") ||
                nom.contains("pasta") ||
                nom.contains("macarrons") ||
                nom.contains("espaguetis") ||
                nom.contains("fideus") ||
                nom.contains("cereals") ||
                nom.contains("farina") ||
                nom.contains("galetes") ||
                nom.contains("torrades") ||
                nom.contains("cuscús") ->
            "Pa i cereals"


        // BEGUDES
        nom.contains("aigua") ||
                nom.contains("coca-cola") ||
                nom.contains("cocacola") ||
                nom.contains("refresc") ||
                nom.contains("suc") ||
                nom.contains("fanta") ||
                nom.contains("sprite") ||
                nom.contains("aquarius") ||
                nom.contains("nestea") ||
                nom.contains("cafè") ||
                nom.contains("cafe") ||
                nom.contains("te ") ||
                nom == "te" ->
            "Begudes"


        // CONGELATS
        nom.contains("pizza congelada") ||
                nom.contains("gelat") ||
                nom.contains("gelats") ||
                nom.contains("croquetes") ||
                nom.contains("nuggets") ||
                nom.contains("patates congelades") ||
                nom.contains("verdura congelada") ||
                nom.contains("peix congelat") ||
                nom.contains("lasanya congelada") ->
            "Congelats"


        // NETEJA
        nom.contains("lleixiu") ||
                nom.contains("detergent") ||
                nom.contains("suavitzant") ||
                nom.contains("rentavaixelles") ||
                nom.contains("fairy") ||
                nom.contains("fregasuelos") ||
                nom.contains("neteja vidres") ||
                nom.contains("netejavidres") ||
                nom.contains("desinfectant") ||
                nom.contains("bosses escombraries") ||
                nom.contains("paper cuina") ||
                nom.contains("esponja") ||
                nom.contains("fregall") ->
            "Neteja"


        // HIGIENE PERSONAL
        nom.contains("xampú") ||
                nom.contains("xampu") ||
                nom.contains("gel de dutxa") ||
                nom.contains("desodorant") ||
                nom.contains("pasta de dents") ||
                nom.contains("raspall de dents") ||
                nom.contains("paper higiènic") ||
                nom.contains("paper higienic") ||
                nom.contains("sabó") ||
                nom.contains("sabo") ||
                nom.contains("crema") ||
                nom.contains("compreses") ||
                nom.contains("tampons") ||
                nom.contains("colònia") ||
                nom.contains("colonia") ->
            "Higiene personal"


        else ->
            "Altres"
    }
}