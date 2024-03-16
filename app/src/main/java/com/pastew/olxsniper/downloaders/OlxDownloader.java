package com.pastew.olxsniper.downloaders;

import android.util.Log;

import com.pastew.olxsniper.db.Offer;
import com.pastew.olxsniper.Utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.InputMismatchException;
import java.util.List;

public class OlxDownloader extends AbstractDownloader {
    @Override
    public List<Offer> getOffersFromUrl(String url) {
        if (!canHandleLink(url)) {
            throw new InputMismatchException();
        }

        String html = WebDownloader.downloadHtml(url);
        return getOffersFromHtml(html);
    }

    @Override
    public List<Offer> getOffersFromHtml(String html) {
        if (html == null || html.isEmpty()) {
            Log.e(TAG, "html is null or empty, can't parse it. Returning empty list");
            return new ArrayList<>();
        }

        Document doc = Jsoup.parse(html);
        Elements elements = doc.getElementsByAttributeValue("data-cy", "l-card"); // Look at example at the bottom of this file

        List<Offer> result = new ArrayList<>();

        if (elements == null) {
            Log.e(TAG, "elements is null. ");
            return result;
        }

        for (Element offerElement : elements) {
            if (offerElement == null) {
                Log.e(TAG, "offerElement is null. ");
                continue;
            }

            String priceString = offerElement.getElementsByAttributeValue("data-testid", "ad-price").text(); // 2 000 zł
            String title = offerElement.getElementsByClass("css-16v5mdi").text();
            String link = "https://" + getLink() + offerElement.getElementsByAttribute("href").first().attr("href"); // /d/oferta/iphone-12-pro-maks-128-gb-CID99-IDZdL0I.html
            String locationDate = offerElement.getElementsByAttributeValue("data-testid", "location-date").text(); // Myszków<!-- --> - <!-- -->Odświeżono dnia 13 marca 2024
            String city = locationDate.split("-")[0].trim(); // Myszków
            // String lastUpdate = locationDate.split("-")[1].trim(); // TODO: Maybe add this to offer? // Odświeżono dnia 13 marca 2024

            Offer o = new Offer(title, Utils.parsePrice(priceString), link, city);

            if (o.promoted && IGNORE_PROMOTED_OFFERS) {
                Log.d(TAG, String.format("Ignored promoted offer: %s", o.link));
            } else {
                result.add(o);
            }
        }

        return result;
    }

    @Override
    String getLink() {
        return "olx.pl";
    }
}

// Example l-card div:
/*
<div data-cy="l-card" data-testid="l-card" id="896998611" class="css-1sw7q4x">
	<a class="css-rc5s2u" href="/d/oferta/apple-iphone-13-128gb-gwarancja-24-miesiace-CID99-IDYHIjx.html">
		<div class="css-qfzx1y">
			<div type="list" class="css-1venxj6">
				<div type="list" class="css-1ut25fa">
					<div type="list" class="css-pn1izb">
						<div class="css-gl6djm">
							<img src="https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=200x0;q=50" srcset="https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=100x0;q=50 100w,
        https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=200x0;q=50 200w,
        https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=300x0;q=50 300w,
        https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=400x0;q=50 400w,
        https://ireland.apollo.olxcdn.com:443/v1/files/kuil9001rg1l-PL/image;s=600x0;q=50 600w" sizes="150px" alt="APPLE IPHONE 13 128GB | Gwarancja 24 miesiące" class="css-8wsg1m"/>
						</div>
						<div class="css-13aawz3">
							<div class="css-1av34ht">
								<div class="css-3xiokn">
									<div data-testid="adCard-featured" class="css-1jh69qu" height="24" font-size="12" font-weight="bold" letter-spacing="0.8" color="background-global-primary">Wyróżnione</div>
									<div class="css-1xwefxo">
										<div class="css-1cigxpj">
											<svg width="1em" height="1em" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" class="css-d7r8uj">
												<path d="M21 15.999h-.343A3.501 3.501 0 0 0 17.5 14a3.501 3.501 0 0 0-3.156 1.997l-4.687.002A3.5 3.5 0 0 0 6.5 14a3.5 3.5 0 0 0-3.158 2L3 16.002V5h11v6l1 1h6v3.999zM17.5 19c-.827 0-1.5-.673-1.5-1.5s.673-1.5 1.5-1.5 1.5.673 1.5 1.5-.673 1.5-1.5 1.5zm-11 0c-.827 0-1.5-.673-1.5-1.5S5.673 16 6.5 16s1.5.673 1.5 1.5S7.327 19 6.5 19zm12-12 2.25 3H16V7h2.5zm1-2H16V4l-1-1H2L1 4v13.002l1.001 1 1.039-.001A3.503 3.503 0 0 0 6.5 21a3.502 3.502 0 0 0 3.46-3l4.08-.003A3.503 3.503 0 0 0 17.5 21a3.502 3.502 0 0 0 3.46-3.001H22l1-1V9.665L19.5 5z" fill="currentColor" fill-rule="evenodd"/>
											</svg>
										</div>
									</div>
								</div>
							</div>
						</div>
					</div>
					<div type="list" class="css-1apmciz">
						<div class="css-u2ayx9">
							<h6 class="css-16v5mdi er34gjf0">APPLE IPHONE 13 128GB | Gwarancja 24 miesiące</h6>
							<p data-testid="ad-price" class="css-10b0gli er34gjf0">
								<span class="css-1c0ed4l">
									<svg width="1em" height="1em" viewBox="0 0 24 24" xmlns="http://www.w3.org/2000/svg" class="css-1ojrdd5">
										<path d="M21 15.999h-.343A3.501 3.501 0 0 0 17.5 14a3.501 3.501 0 0 0-3.156 1.997l-4.687.002A3.5 3.5 0 0 0 6.5 14a3.5 3.5 0 0 0-3.158 2L3 16.002V5h11v6l1 1h6v3.999zM17.5 19c-.827 0-1.5-.673-1.5-1.5s.673-1.5 1.5-1.5 1.5.673 1.5 1.5-.673 1.5-1.5 1.5zm-11 0c-.827 0-1.5-.673-1.5-1.5S5.673 16 6.5 16s1.5.673 1.5 1.5S7.327 19 6.5 19zm12-12 2.25 3H16V7h2.5zm1-2H16V4l-1-1H2L1 4v13.002l1.001 1 1.039-.001A3.503 3.503 0 0 0 6.5 21a3.502 3.502 0 0 0 3.46-3l4.08-.003A3.503 3.503 0 0 0 17.5 21a3.502 3.502 0 0 0 3.46-3.001H22l1-1V9.665L19.5 5z" fill="currentColor" fill-rule="evenodd"/>
									</svg>
								</span>1 799 zł</p>
						</div>
						<div class="css-krg4hw">
							<div class="css-1o56lv1">
								<span>
									<span title="Używane" class="css-3lkihg">
										<span>Używane</span>
									</span>
								</span>
							</div>
						</div>
						<div class="css-odp1qd">
							<p data-testid="location-date" class="css-1a4brun er34gjf0">Myszków<!-- --> - <!-- -->Odświeżono dnia 13 marca 2024</p>
							<div color="text-global-secondary" class="css-1kfqt7f"/>
						</div>
						<span data-testid="adAddToFavorites" class="css-1x8zoa0">
							<div class="css-dxswhb">
								<svg xmlns="http://www.w3.org/2000/svg" viewBox="0 0 24 24" width="1em" height="1em" class="css-znbvx0">
									<path fill="currentColor" fill-rule="evenodd" d="M20.219 10.367 12 20.419 3.806 10.4A3.96 3.96 0 0 1 3 8c0-2.206 1.795-4 4-4a4.004 4.004 0 0 1 3.868 3h2.264A4.003 4.003 0 0 1 17 4c2.206 0 4 1.794 4 4 0 .868-.279 1.698-.781 2.367M17 2a5.999 5.999 0 0 0-5 2.686A5.999 5.999 0 0 0 7 2C3.692 2 1 4.691 1 8a5.97 5.97 0 0 0 1.232 3.633L10.71 22h2.582l8.501-10.399A5.943 5.943 0 0 0 23 8c0-3.309-2.692-6-6-6"/>
								</svg>
								<div data-testid="favorite-icon" class="css-5xgpg7">Obserwuj</div>
							</div>
						</span>
					</div>
				</div>
			</div>
		</a>
	</div>
 */