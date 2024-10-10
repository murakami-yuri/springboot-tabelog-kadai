package com.example.kadai002.service;

import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.kadai002.entity.User;
import com.stripe.Stripe;
import com.stripe.exception.ApiConnectionException;
import com.stripe.exception.ApiException;
import com.stripe.exception.AuthenticationException;
import com.stripe.exception.InvalidRequestException;
import com.stripe.exception.PermissionException;
import com.stripe.exception.RateLimitException;
import com.stripe.exception.StripeException;
import com.stripe.model.Customer;
import com.stripe.model.CustomerCollection;
import com.stripe.model.Event;
import com.stripe.model.SetupIntent;
import com.stripe.model.StripeObject;
import com.stripe.model.Subscription;
import com.stripe.model.SubscriptionCollection;
import com.stripe.model.checkout.Session;
import com.stripe.param.CustomerListParams;
import com.stripe.param.CustomerUpdateParams;
import com.stripe.param.SubscriptionCancelParams;
import com.stripe.param.SubscriptionListParams;
import com.stripe.param.checkout.SessionCreateParams;
import com.stripe.param.checkout.SessionCreateParams.Mode;
import com.stripe.param.checkout.SessionCreateParams.PaymentMethodType;

import jakarta.annotation.PostConstruct;

@Service
public class StripeService {
	
	@Autowired
	private UserService userService;
	
	@Autowired
	//private PaymentService paymentService;
	
	private static final PaymentMethodType PAYMENT_METHOD_TYPE = SessionCreateParams.PaymentMethodType.CARD;  // 決済方法
    private static final String CURRENCY = "jpy";  // 通貨
    private static final long QUANTITY = 1L;  // 数量
    private static final Mode MODE_SUBSCRIPTION = SessionCreateParams.Mode.SUBSCRIPTION;  // 支払いモード
    private static final Mode MODE_SETUP = SessionCreateParams.Mode.SETUP;  // 支払いモード
    //private static final String SUCCESS_URL = "http://localhost:8080/user?upgraded";  // 決済成功時のリダイレクト先URL
    //private static final String CANCEL_URL = "http://localhost:8080/user/upgrade";  // 決済キャンセル時のリダイレクト先URL
    //private static final DateTimeFormatter DATE_TIME_FORMATTER  = DateTimeFormatter.ofPattern("yyyy-MM-dd");  // 日付のフォーマット
    private static final String priceId = "price_1Q84xQP4ZZidmJveNi68SX3w";
    
    // Stripeのシークレットキー
    @Value("${stripe.api-key}")
    private String stripeApiKey; 
    
    // 決済成功時のリダイレクト先URL 
    @Value("${stripe.success-url}")
    private String stripeSuccessUrl;

    // 決済キャンセル時のリダイレクト先URL
    @Value("${stripe.cancel-url}")
    private String stripeCancelUrl;  

    // クレジットカード登録成功時のリダイレクト先URL 
    @Value("${stripe.success-card-url}")
    private String stripeSuccessCardUrl;
    
    // 依存性の注入後に一度だけ実行するメソッド
    @PostConstruct
    private void init() {
        // Stripeのシークレットキーを設定する
        Stripe.apiKey = stripeApiKey;
    }
    
    // サブスクリプションの開始
    // StripeにセッションをAPI経由で送付
    public String createStripeSession(User user) {

        String userId = user.getId().toString();
        
        // セッションに入れる支払い情報
        SessionCreateParams sessionCreateParams =
            SessionCreateParams.builder()
                .addPaymentMethodType(PAYMENT_METHOD_TYPE)
                .addLineItem(
                    SessionCreateParams.LineItem.builder()
                        .setQuantity(QUANTITY)
                        .setPrice(priceId)
                        .build())
                .setMode(MODE_SUBSCRIPTION)
                .setSuccessUrl(stripeSuccessUrl)
                .setCancelUrl(stripeCancelUrl)
                .putMetadata("userId", userId)
                .build();

        try {
            // Stripeに送信する支払い情報をセッションとして作成する
            Session session = Session.create(sessionCreateParams);

            // 作成したセッションのIDを返す
            return session.getId();
        } catch (RateLimitException e) {
            System.out.println("短時間のうちに過剰な回数のAPIコールが行われました。");
            return "";
        } catch (InvalidRequestException e) {
            System.out.println("APIコールのパラメーターが誤っているか、状態が誤っているか、方法が無効でした。");
            return "";
        } catch (PermissionException e) {
            System.out.println("このリクエストに使用されたAPIキーには必要な権限がありません。");
            return "";
        } catch (AuthenticationException e) {
            System.out.println("Stripeは、提供された情報では認証できません。");
            return "";
        } catch (ApiConnectionException e) {
            System.out.println("お客様のサーバーとStripeの間でネットワークの問題が発生しました。");
            return "";
        } catch (ApiException e) {
            System.out.println("Stripe側で問題が発生しました（稀な状況です）。");
            return "";
        } catch (StripeException e) {
            System.out.println("Stripeとの通信中に予期せぬエラーが発生しました。");
            return "";
        }
    }
    
    // クレジットカードの変更
    // StripeにセッションをAPI経由で送付
    public String createStripeSessionUpdate(User user) {
        
    	Customer customer = getCsutomerIdByEmail(user.getEmail());
    	String customerId = customer.getId();
        //Payment payment = paymentService.findPaymentByUser(user);
        //String customerId = payment.getCustomerId();
        System.out.println("*----------------CustomerId" + customerId);
        
        // セッションに入れる支払い情報
        SessionCreateParams sessionCreateParams =
            SessionCreateParams.builder()
                .addPaymentMethodType(PAYMENT_METHOD_TYPE)
                .setMode(MODE_SETUP)
                .setCurrency(CURRENCY)
                .setCustomer(customerId)
                .setSuccessUrl(stripeSuccessCardUrl)
                .setCancelUrl(stripeCancelUrl)
                //.putMetadata("userId", user.getId().toString())
                .build();

        try {
            // Stripeに送信する支払い情報をセッションとして作成する
            Session session = Session.create(sessionCreateParams);

            // 作成したセッションのIDを返す
            return session.getId();
        } catch (RateLimitException e) {
            System.out.println("短時間のうちに過剰な回数のAPIコールが行われました。");
            return "";
        } catch (InvalidRequestException e) {
            System.out.println("APIコールのパラメーターが誤っているか、状態が誤っているか、方法が無効でした。");
            return "";
        } catch (PermissionException e) {
            System.out.println("このリクエストに使用されたAPIキーには必要な権限がありません。");
            return "";
        } catch (AuthenticationException e) {
            System.out.println("Stripeは、提供された情報では認証できません。");
            return "";
        } catch (ApiConnectionException e) {
            System.out.println("お客様のサーバーとStripeの間でネットワークの問題が発生しました。");
            return "";
        } catch (ApiException e) {
            System.out.println("Stripe側で問題が発生しました（稀な状況です）。");
            return "";
        } catch (StripeException e) {
            System.out.println("Stripeとの通信中に予期せぬエラーが発生しました。");
            return "";
        }
    }
    
    // サブスクリプションの開始、クレジットカードの変更の後処理
    // セッションからユーザー情報を取得し、データベースに登録する
    @Transactional
    public void processSessionCompleted(Event event) {

        // EventオブジェクトからStripeObjectオブジェクトを取得する
        Optional<StripeObject> optionalStripeObject = event.getDataObjectDeserializer().getObject();

        optionalStripeObject.ifPresentOrElse(stripeObject -> {

            // StripeObjectオブジェクトをSessionオブジェクトに型変換する
            Session session = (Session)stripeObject;

            String mode = session.getMode();
            System.out.println("*----------------" + mode);
            
            // 詳細なセッション情報からメタデータを取り出す
            Map<String, String> sessionMetadata = session.getMetadata();
            // ユーザーIDを取得
            Integer userId = Integer.valueOf(sessionMetadata.get("userId"));
            
            try {
            	// サブスクリプション申請の場合
            	if (mode.equals("subscription")) {
	                
	                // Userテーブルの有料会員フラグをTrueにする
	                userService.upgradeUser(userId);
	
	                System.out.println("有料会員へのアップグレードの登録処理が成功しました。");
            	}
            	// クレジットカード変更の場合
            	if (mode.equals("setup")) {
            		// セッションから作成したsetupIntentとcustomerIDを取得
            		String setupIntentId = session.getSetupIntent();
            		String customerId = session.getCustomer();
            		
            		// setupIntentからpaymentMethodを取得
            		SetupIntent setupIntent = SetupIntent.retrieve(setupIntentId);
            		String paymentMethodId = setupIntent.getPaymentMethod();
            		
            		// カスタマーインスタンスを取得
            		Customer customer = Customer.retrieve(customerId);
            		
            		// 変更前の支払い状態の確認
            		String defaultPaymentMethodId = customer.getInvoiceSettings().getDefaultPaymentMethod();
            		
            		if (defaultPaymentMethodId != null) {
            		    System.out.println("------------before: Current default payment method ID: " + defaultPaymentMethodId);
            		} else {
            		    System.out.println("------------before: No default payment method is set.");
            		}
            		
            		// 支払い方法のデフォルトの変更
            		CustomerUpdateParams params = CustomerUpdateParams.builder()
            				.setInvoiceSettings(CustomerUpdateParams.InvoiceSettings.builder()
            				        .setDefaultPaymentMethod(paymentMethodId)
            				        .build())
            				.build();
            		
            		// 変更後の支払い状態の確認
            		Customer customerNew = customer.update(params);
            		
            		String defaultPaymentMethodIdNew = customerNew.getInvoiceSettings().getDefaultPaymentMethod();
            		
            		if (defaultPaymentMethodIdNew != null) {
            		    System.out.println("------------after: Current default payment method ID: " + defaultPaymentMethodIdNew);
            		} else {
            		    System.out.println("------------after: No default payment method is set.");
            		}
            	}
            	else {
            		System.out.println("未対応のセッションモードです。");
            	}

            } catch (RateLimitException e) {
                System.out.println("短時間のうちに過剰な回数のAPIコールが行われました。");
            } catch (InvalidRequestException e) {
                System.out.println("APIコールのパラメーターが誤っているか、状態が誤っているか、方法が無効でした。");
            } catch (PermissionException e) {
                System.out.println("このリクエストに使用されたAPIキーには必要な権限がありません。");
            } catch (AuthenticationException e) {
                System.out.println("Stripeは、提供された情報では認証できません。");
            } catch (ApiConnectionException e) {
                System.out.println("お客様のサーバーとStripeの間でネットワークの問題が発生しました。");
            } catch (ApiException e) {
                System.out.println("Stripe側で問題が発生しました（稀な状況です）。");
            } catch (StripeException e) {
                System.out.println("Stripeとの通信中に予期せぬエラーが発生しました。");
            } catch (Exception e) {
                System.out.println("有料会員へのアップグレードの登録処理中に予期せぬエラーが発生しました。");
            }
        },
        () -> {
            System.out.println("有料会員へのアップグレードの登録処理が失敗しました。");
        });
     		    
        // StripeのAPIとstripe-javaライブラリのバージョンをコンソールに出力する
        System.out.println("Stripe API Version: " + event.getApiVersion());
        System.out.println("stripe-java Version: " + Stripe.VERSION + ", stripe-java API Version: " + Stripe.API_VERSION);
    }
    
    //　サブスクリプションの解約
    @Transactional
    public void cancelSubscription(String subscriptionId, Integer userId) throws StripeException  { 		

		Subscription subscription = Subscription.retrieve(subscriptionId);
		SubscriptionCancelParams params = SubscriptionCancelParams.builder().build();
		subscription.cancel(params);
		
		// Userテーブルの有料会員フラグをFalseにする
        userService.downgradeUser(userId);
	
    }
    
    // メールアドレスからカスタマーを取得する
    public Customer getCsutomerIdByEmail(String email) {
    	
    	Customer targetCustomer = null;
    	CustomerCollection customers = new CustomerCollection();
    	
        // ステップ1: 顧客リストを取得して、特定のメールアドレスを持つ顧客を探す
        CustomerListParams customerParams = CustomerListParams.builder()
                .setLimit(100L)
                .setEmail(email) // メールアドレスでフィルタリング
                .build();
        
        try {
        	customers = Customer.list(customerParams);

            for (Customer customer : customers.getData()) {
                if (customer.getEmail().equals(email)) {
                    targetCustomer = customer;
                    break;
                }
            }
            
        } catch (StripeException e) {
        	System.out.println("Stripeとの通信中に予期せぬエラーが発生しました。");
        }
        
        return targetCustomer;
    }
    
    // メールアドレスからサブスクリプションIDを取得する
    public String getSubscriptionIdByEmail(String email) {
    	
    	String subscriptionId = "";
        
        try {
        	
        	Customer targetCustomer = getCsutomerIdByEmail(email);
            
            if (targetCustomer != null) {
                // ステップ2: 顧客のサブスクリプションを取得
                SubscriptionListParams subscriptionParams = SubscriptionListParams.builder()
                        .setCustomer(targetCustomer.getId())
                        .setLimit(100L)
                        .build();

                SubscriptionCollection subscriptions = Subscription.list(subscriptionParams);

                for (Subscription subscription : subscriptions.getData()) {
                    if (subscription.getItems().getData().stream().anyMatch(item ->
                            item.getPrice().getId().equals(priceId))) {
                    	subscriptionId = subscription.getId();
                        break;
                    }
                }
            } 
        } catch (StripeException e) {
        	System.out.println("Stripeとの通信中に予期せぬエラーが発生しました。");
        }
        
        return subscriptionId;
    }
}
