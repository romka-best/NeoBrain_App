"""Upgrade DB

Revision ID: aaa215ee08c9
Revises: 9f6821109f17
Create Date: 2020-04-10 21:25:22.832605

"""
from alembic import op
import sqlalchemy as sa

# revision identifiers, used by Alembic.
revision = 'aaa215ee08c9'
down_revision = '9f6821109f17'
branch_labels = None
depends_on = None


def upgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.create_table('chat_association',
                    sa.Column('user', sa.Integer(), nullable=True),
                    sa.Column('chat', sa.Integer(), nullable=True),
                    sa.ForeignKeyConstraint(['chat'], ['chats.id'], ),
                    sa.ForeignKeyConstraint(['user'], ['users.id'], )
                    )
    op.create_table('photo_association',
                    sa.Column('user', sa.Integer(), nullable=True),
                    sa.Column('photo', sa.Integer(), nullable=True),
                    sa.ForeignKeyConstraint(['photo'], ['photos.id'], ),
                    sa.ForeignKeyConstraint(['user'], ['users.id'], )
                    )
    op.drop_table('association')
    op.alter_column('chats', 'name',
                    existing_type=sa.VARCHAR(),
                    nullable=True)
    # ### end Alembic commands ###


def downgrade():
    # ### commands auto generated by Alembic - please adjust! ###
    op.alter_column('chats', 'name',
                    existing_type=sa.VARCHAR(),
                    nullable=False)
    op.create_table('association',
                    sa.Column('user', sa.INTEGER(), nullable=True),
                    sa.Column('chat', sa.INTEGER(), nullable=True),
                    sa.ForeignKeyConstraint(['chat'], ['chats.id'], ),
                    sa.ForeignKeyConstraint(['user'], ['users.id'], )
                    )
    op.drop_table('photo_association')
    op.drop_table('chat_association')
    # ### end Alembic commands ###
