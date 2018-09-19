load('api_config.js'); 
load('api_wifi.js'); 
load('api_rpc.js'); 
load('api_gpio.js'); 
load('api_http.js'); 
 
 
 

  let mIp;
  RPC.addHandler('reg',function(args){
        
        print(JSON.stringify(args));
        mIp=args.ip;
        return {result:true,ip:args.ip};
        
  });
  
  
let button = Cfg.get('pins.button');
  GPIO.set_button_handler(button, GPIO.PULL_UP, GPIO.INT_EDGE_NEG, 20, function() {
		
			print("Clicked ! btn ",mIp);
			
			HTTP.query({
				url: mIp,
				success: function(body, full_http_msg) {
					print(body); 
				},
				error: function( s ) { print(s); },  
			}); 

}, null);

  GPIO.set_button_handler(5, GPIO.PULL_UP, GPIO.INT_EDGE_NEG, 20, function() {
		
			print("Clicked ! 5 ",mIp );
			HTTP.query({
				url: mIp,
				success: function(body, full_http_msg) {
					print(body); 
				},
				error: function( s ) { print(s); },  
			}); 

}, null);
	