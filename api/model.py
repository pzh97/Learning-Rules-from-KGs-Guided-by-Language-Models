import torch
from transformers import BertTokenizer, BertForMaskedLM
import json
import scipy

class Model:
    def __init__(self):
        self.tokenizer = BertTokenizer.from_pretrained('bert-base-uncased')
        self.model = BertForMaskedLM.from_pretrained('bert-base-uncased')
        self.model.eval()
        
        with open("../data/wiki44k/entities2tokens.json", "r") as infile:
            self.entities2tokens = json.load(infile)      
        self.tokens_indices = torch.tensor(list(self.entities2tokens.values())).unique()
        
        self.neg_tokens_mask = torch.ones((len(self.tokenizer.vocab))).bool()
        self.neg_tokens_mask[self.tokens_indices] = 0

    def predict(self, head, relation, tail, top_k = 100):

        text = f"{head} {relation} [MASK]."
        tokenized_text = self.tokenizer(text, return_tensors="pt").input_ids
        masked_index = ((tokenized_text[0] == self.tokenizer.mask_token_id).nonzero(as_tuple=True)[0]).item()
        target_index = self.entities2tokens[tail]
        
        with torch.no_grad():
            outputs = self.model(input_ids=tokenized_text)
            predictions = outputs[0][0, masked_index].reshape(-1)

        predictions[self.neg_tokens_mask] = float("-inf") 
        ranks = scipy.stats.rankdata(-1. * predictions)
        
        rank = ranks[target_index]    
        
        return 1 / rank

#model = Model()
#print(model.predict("Dante was born in  [MASK].", top_k=10))


