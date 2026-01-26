package scraper;

import model.JobAnnouncement;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import utils.DataNormalizer;
import javax.net.ssl.*;
import java.io.IOException;
import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.X509Certificate;
import java.util.ArrayList;
import java.util.List;

public class AnapecOrgScraper implements SiteScraper {

    private static final String BASE_URL =
            "http://www.anapec.org/sigec-app-rv/fr/chercheurs/resultat_recherche/tout:all";
    private static final String BASE_DOMAIN = "http://www.anapec.org";

    static {
        disableSSLVerification();
    }

    @Override
    public List<JobAnnouncement> scrape() {

        List<JobAnnouncement> jobs = new ArrayList<>();
        int page = 1;
        int emptyPages = 0;

        System.out.println("🕷️ Scraping ANAPEC.org — TOUTES les pages");

        while (true) {
            try {
                String pageUrl = (page == 1)
                        ? BASE_URL
                        : BASE_URL + "/page:" + page;

                System.out.println("📄 Page " + page + " → " + pageUrl);

                Document doc = Jsoup.connect(pageUrl)
                        .userAgent("Mozilla/5.0")
                        .timeout(15000)
                        .get();

                Elements rows = doc.select("tbody.header tr");
                System.out.println("➡ Offres trouvées : " + rows.size());

                if (rows.isEmpty()) {
                    emptyPages++;
                    if (emptyPages >= 2) {
                        System.out.println("🛑 Fin du scraping (plus d’offres)");
                        break;
                    }
                    page++;
                    continue;
                }

                emptyPages = 0;

                for (Element row : rows) {
                    JobAnnouncement job = extractJobFromRow(row);
                    if (job != null && job.getSourceUrl() != null) {
                        scrapeJobDetails(job);

                        jobs.add(job);
                    }
                }

                page++;
                Thread.sleep(2000);

            } catch (Exception e) {
                System.err.println("❌ Erreur page ANAPEC : " + e.getMessage());
                break;
            }
        }

        System.out.println("✅ ANAPEC terminé : " + jobs.size() + " offres\n");

        return jobs;
    }

    private JobAnnouncement extractJobFromRow(Element row) {

        try {
            Elements tds = row.select("td");
            if (tds.size() < 7) return null;

            JobAnnouncement job = new JobAnnouncement();

            // URL + référence
            Element refLink = tds.get(1).selectFirst("a");
            if (refLink != null) {
                String url = refLink.attr("href")
                        .replace("/resultat_recherche", "/display");
                job.setSourceUrl(BASE_DOMAIN + url);
                job.setDescription("Référence : " + refLink.text());
            }

            job.setPublishDateString(tds.get(2).text().trim());
            job.setTitle(tds.get(3).text().trim());

            try {
                job.setNombrePostes(Integer.parseInt(tds.get(4).text().trim()));
            } catch (NumberFormatException e) {
                job.setNombrePostes(1);
            }

            Element img = tds.get(5).selectFirst("img");
            if (img != null && !img.attr("alt").isEmpty()) {
                job.setCompany(img.attr("alt").replace("pas de logo", "").trim());
            }

            job.setLocation(tds.get(6).text().trim());

            return job;

        } catch (Exception e) {
            return null;
        }
    }

    private void scrapeJobDetails(JobAnnouncement job) {
        try {
            Document doc = Jsoup.connect(job.getSourceUrl())
                    .userAgent("Mozilla/5.0")
                    .timeout(15000)
                    .get();

            StringBuilder desc = new StringBuilder();
            for (Element p : doc.select("p")) {
                desc.append(p.text()).append("\n");
            }

            job.setDescription(desc.toString().trim());

        } catch (IOException e) {
            System.err.println("⚠️ Détails ANAPEC non récupérés");
        }
    }

    private static void disableSSLVerification() {
        try {
            TrustManager[] trustAllCerts = new TrustManager[]{
                    new X509TrustManager() {
                        public X509Certificate[] getAcceptedIssuers() { return null; }
                        public void checkClientTrusted(X509Certificate[] c, String a) {}
                        public void checkServerTrusted(X509Certificate[] c, String a) {}
                    }
            };
            SSLContext sc = SSLContext.getInstance("SSL");
            sc.init(null, trustAllCerts, new java.security.SecureRandom());
            HttpsURLConnection.setDefaultSSLSocketFactory(sc.getSocketFactory());
            HttpsURLConnection.setDefaultHostnameVerifier((h, s) -> true);
        } catch (NoSuchAlgorithmException | KeyManagementException ignored) {}
    }

    public String getSiteName() {
        return "ANAPEC";
    }
}