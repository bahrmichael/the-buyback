import { BaseEntity } from './../../shared';

export const enum TypeCategory {
    'COMPRESSED_ORE',
    'MOON_ORE',
    'ICE_PRODUCTS',
    'BIG_PLANETARY',
    'GENERAL'
}

export class TypeBuybackRate implements BaseEntity {
    constructor(
        public id?: string,
        public typeId?: number,
        public typeName?: string,
        public category?: TypeCategory,
        public rate?: number,
    ) {
    }
}
