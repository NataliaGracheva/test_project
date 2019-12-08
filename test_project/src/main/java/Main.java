import org.openqa.selenium.By;
import org.openqa.selenium.WebDriver;
import org.openqa.selenium.chrome.ChromeDriver;
import org.openqa.selenium.support.ui.ExpectedConditions;
import org.openqa.selenium.support.ui.WebDriverWait;
import java.util.concurrent.TimeUnit;

public class Main {

    public static void main(String[] args) {

        String projectPath = System.getProperty("user.dir");
        System.setProperty("webdriver.chrome.driver", projectPath+"\\drivers\\chromedriver\\chromedriver.exe");
        WebDriver driver = new ChromeDriver();

        driver.manage().timeouts().implicitlyWait(5, TimeUnit.SECONDS);
        driver.manage().window().maximize();

        driver.get("https://k-vrachu.ru/");

        // перейти в демо режим
        driver.findElement(By.cssSelector("a.demo")).click();
        driver.findElement(By.cssSelector(".demoBox a")).click();

        // если не удалось перейти в демо режим, сменить регион и попробовать снова
        if (driver.findElements(By.cssSelector("div.top-alert")).size() == 0) {
            System.out.println("не удается перейти в демо режим");
            driver.findElement(By.cssSelector(".region ul")).click();
            driver.findElement(By.cssSelector(".region a")).click();
            driver.findElement(By.cssSelector("a.demo")).click();
            driver.findElement(By.cssSelector(".demoBox a")).click();
        }

        // если перешли в демо режим, выбрать любого врача, к которому возможна запись
        if (driver.findElements(By.cssSelector("div.top-alert")).size() > 0) {
            System.out.println("демо режим активирован, пытаюсь записать к врачу");
            driver.findElement(By.linkText("Записать к врачу")).click();
            driver.findElement(By.cssSelector(".selectDoc li:not(.disabledSpec) a")).click();
            driver.findElement(By.cssSelector(".docsInLpuTableDetail span.nearest-record:not(:empty)")).click();

            // если нет записи на ближайшее время, проверить далее
            if (driver.findElements(By.cssSelector(".timeTable td.free")).size() == 0) {
                System.out.println("нет записи на ближайшее время");
                driver.findElement(By.cssSelector("div.timeTableWeekArrowsRight")).click();
            }

            // если есть свободное время, записаться к врачу
            if (driver.findElements(By.cssSelector(".timeTable td.free")).size() > 0) {
                System.out.println("записываю к врачу");
                driver.findElement(By.cssSelector(".timeTable td.free")).click();

                // если открылось окно подтверждения, нажать на кнопку Подтвердить
                if (driver.findElements(By.cssSelector(".regAgree a.record-button")).size() > 0) {
                    System.out.println("подтверждение записи");
//                    System.out.println(driver.findElement(By.cssSelector(".regAgree a.record-button")).getText());
                    WebDriverWait wait = new WebDriverWait(driver, 10);
                    wait.until(ExpectedConditions.elementToBeClickable(By.cssSelector(".regAgree a.record-button")));
                    // click не срабатывает
                    driver.findElement(By.cssSelector(".regAgree a.record-button")).click();

                    // если появилось сообщение об ошибке - тест пройден
                    if (driver.findElements(By.cssSelector(".http-error h1")).size() > 0) {
                        if (driver.findElement(By.cssSelector(".http-error h1")).getText().equals("Запись в базу данных невозможна")) {
                            driver.findElement(By.cssSelector(".top-alert a.alert_button")).click();
                            System.out.println("test passed");
                        }
                    }
                }
            }
        }
        driver.quit();
    }
}
